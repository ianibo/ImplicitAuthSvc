package implicitauth

import grails.converters.JSON;
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import org.jose4j.jwk.*
import org.jose4j.jwt.*
import org.jose4j.jws.*
import javax.servlet.http.Cookie
import java.net.URLEncoder


/**
 *  Overall flow.
 *  Client asks for login, redux-oauth opens a popup to /auth/oauth/{provider} which arrives at redirectToIDP
 *  redirectToIDP redirects to approproiate service - eg google
 *  IDP Validates and if valid sends a redirect to /auth/oauth/{provider}/callback#{TOKEN}
 *    the # prevents the JS app from detecting the TOKEN or passing it to the server
 *    so we dispatch a script which can then access the location.href.hash containing the token (callback#access_token=xxxxx)
 *  The script extracts the access_token and passes this back to the server which needs to create an auth_token from the access_token
 *  Client gets completeAuth which consults the appropriate provider to get back user details and constructs a JWT
 *
 *  In the demo app, callback causes a redirect to a URL like
 *    auth_token=lMTuR3FqmBFHD0WD7URq9g&blank=true&client_id=UT3fEq5-vHLBqPmYvx8_pw&config=&expiry=1497272025&uid=53283 which is then picked up and used as the auth string
 *
 *
 */

class AuthController {

  def publicKeyService

  def index() { 
    log.debug("AuthController::index");
  }

  def redirectToIDP() {
    log.debug("AuthController::redirectToIDP() ${params}");

    if ( params.provider ) {
      def authorization_svc = OAuthAuthorizationService.findByCode(params.provider)

      if ( authorization_svc ) {
        redirect(url:authorization_svc.baseUrl+
                '?scope='+java.net.URLEncoder.encode(authorization_svc.scope,'UTF-8')+
                '&redirect_uri='+java.net.URLEncoder.encode(authorization_svc.redirectUrl,'UTF-8')+
                '&response_type='+java.net.URLEncoder.encode('token','UTF-8')+  // token
                '&client_id='+java.net.URLEncoder.encode(authorization_svc.clientId,'UTF-8')
              );
      }
      else {
        log.error("Unable to locate provider");
      }
    }
  }

  // The callback method is called but the browser keeps the access_token in a fragment identifier - EG
  // http://localhost:8080/auth/oauth/google/callback#access_token=xxx&token_type=Bearer&expires_in=3600
  // so we need to send back a script which will extract the access_token from the fragment and then call a further validate_token function
  def callback() {
    log.debug("AuthController::callback ${params}");
  }

  def validateToken() {
    log.debug("AuthController::validateToken() ${params}");
    def response = [:]

    log.debug("Auth header: ${request.getHeader('Authorization')}");
    def authorization_header = request.getHeader("Authorization")
    if ( authorization_header ) {
      log.debug("Got auth header");
      def token = authorization_header.split(' ')[1];
      def payload = publicKeyService.decodeJWT(token)
      log.debug("Got payload \"${payload}\" subject:\"${payload?.subject}\" from ${token}");
      log.debug("Got payload claims map \"${payload?.getClaimsMap()}\"");
      def claims_as_map = payload?.getClaimsMap()
      response.data=[:]
      response.data.name=claims_as_map.displayName
      response.data.uid=claims_as_map.sub
    }


    // Docs says we send back auth info in cookie called authHeaders
    render response as JSON
  }

  def completeAuth() {
    log.debug("AuthController::completeAuth() ${params}");
    def response = [:]

    if ( ( params.provider?.length() > 0 )  && ( params.access_token?.length() > 0 ) ) {
      def authorization_svc = OAuthAuthorizationService.findByCode(params.provider)
      if ( authorization_svc != null ) {
        def token_result = exchangeAuthCodeForToken(params.access_token,authorization_svc)
      }
      else {
        log.error("Unable to locate provider");
      }
    }
    else {
      log.error("No provider or access token in call to validateToken");
    }
  }

  private def exchangeAuthCodeForToken(String token, provider_cfg) {
    log.debug("exchangeAuthCodeForToken(${token},${provider_cfg} ${params}");

    def user = null;
    // user.username="wibble"
    switch ( provider_cfg.code ) {
      case 'google':
        user = processGoogle(token,provider_cfg);
        break;
      default:
        break;
    }

    // Our response auth_token is a jwt containing user details
    def jwt = createToken(user, provider_cfg.code)

    // redux-oauth would like us to set an authHeaders cookie so that we can store the token
    // Assuming the controller lets this private method access the response object for this request
    log.debug("Set authHeaders cookie with json document containing info about the token");
    def auth_headers_data = [
      auth_token:jwt,
      token_type:'Bearer',
      client_id:provider_cfg.clientId
    ]
    def auth_headers_as_json = auth_headers_data as JSON
    Cookie cookie = new Cookie( "authHeadersTest", URLEncoder.encode(auth_headers_as_json.toString()) );
    cookie.maxAge = 315360000000L
    response.addCookie( cookie )

    log.debug("Redirecting..."+jwt);
    redirect(url:'http://localhost:8081/?auth_token='+jwt+'&token_type=Bearer&client_id='+provider_cfg.clientId+
                 '&config=&expiry=1497272025');
                 // '&config=&expiry=1497272025&uid=53283');
  }


  private def processGoogle(String access_token, provider_cfg) {

    def result = null;

    log.debug("processGoogle(${access_token},${provider_cfg})");

    // get the URI to hit for obtaining meta-data about the user from the social API.
    // google userinfo endpoint moving to  https://www.googleapis.com/plus/v1/people/{userId} and {userId} can be /me
    def peopleUri = 'https://www.googleapis.com/oauth2/v1/userinfo'.toURI()

    // Locate a user for...
    def people_api = new HTTPBuilder(peopleUri.scheme + "://" + peopleUri.host)
    people_api.ignoreSSLIssues()

    try {
      log.debug("Fetch the person data via the people URI -- ${peopleUri} api -- ${peopleUri?.scheme}://${peopleUri?.host} auth:${access_token}")

      people_api.request(GET,groovyx.net.http.ContentType.JSON) { req ->

        uri.path = peopleUri.path
        //uri.query = auth_cfg.query
        headers.'Authorization' = 'Bearer ' + access_token
        headers.Accept = 'application/json'

        response.success = { r2, j2 ->
          log.debug("response: ${r2} ${j2}");

      
          SocialIdentity si = SocialIdentity.findByProviderAndReference('google',j2.id) 
          if ( si == null ) {
            log.debug("Unable to locate social identity for 'google' and ${j2.id}");
            log.debug("Create user");

            result = new implicitauth.User();
            result.username=java.util.UUID.randomUUID().toString()
            // Should really generate a slug here from the j2.name
            result.uriName=result.username
            result.password=java.util.UUID.randomUUID().toString()
            result.enabled=true
            result.accountExpired=false
            result.accountLocked=false
            result.passwordExpired=false
            result.localId=null
            result.profilePic=j2.picture
            result.email=j2.email
            result.biography=null
            result.displayName=j2.name

            log.debug("Save new user: ${result} (${result.username} ${result.password})");
            result.save(flush:true, failOnError:true);

            log.debug("Create social identity");
            si = new SocialIdentity(provider:'google', reference:j2.id, user:result).save(flush:true, failOnError:true);

            log.debug("Allocate ROLE_USER to new user");
            def role_user = Role.findByAuthority('ROLE_USER') ?: new Role(authority:'ROLE_USER');
            def new_grant = new UserRole(role:role_user, user:result).save(flush:true, failOnError:true)
          }
          else {
            result = si.user;
          }
        }
        response.failure = { resp2, reader ->
          log.error("Failure result ${resp2.statusLine}")
          log.error(reader.text)
        }
      }
    }
    catch ( Exception e ) {
      log.error("Problem fetching user data",e);
    }


    return result
  }

  private String createToken(user, provider) {

    log.debug("Request seems to contain a legitimate user - create and sign a token for that user");

    // See https://bitbucket.org/b_c/jose4j/wiki/JWT%20Examples

    RsaJsonWebKey rsaJsonWebKey = publicKeyService.getAppPublicKey()
    // log.debug("Got app public key ${rsaJsonWebKey}");
    // RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
    // Give the JWK a Key ID (kid), which is just the polite thing to do
    // rsaJsonWebKey.setKeyId("k1");

    // Create the Claims, which will be the content of the JWT
    JwtClaims claims = new JwtClaims()
    claims.setIssuer(publicKeyService.AUTH_AUD)  // who creates the token and signs it
    claims.setAudience(publicKeyService.AUTH_AUD) // to whom the token is intended to be sent
    claims.setExpirationTimeMinutesInTheFuture(60*15) // time when the token will expire (60*15 minutes from now)
    claims.setGeneratedJwtId() // a unique identifier for the token
    claims.setIssuedAtToNow();  // when the token was issued/created (now)
    claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
    claims.setSubject(user.username); // the subject/principal is whom the token is about
    claims.setClaim("email",user.email); // additional claims/attributes about the subject can be added
    claims.setClaim("displayName",user.displayName); // additional claims/attributes about the subject can be added
    // List<String> groups = Arrays.asList("group-one", "other-group", "group-three");
    // claims.setStringListClaim("groups", groups); // multi-valued claims work too and will end up as a JSON array

    // A JWT is a JWS and/or a JWE with JSON claims as the payload.
    // In this example it is a JWS so we create a JsonWebSignature object.
    JsonWebSignature jws = new JsonWebSignature();

    // The payload of the JWS is JSON content of the JWT Claims
    jws.setPayload(claims.toJson());

    // The JWT is signed using the private key
    jws.setKey(rsaJsonWebKey.getPrivateKey());

    // Set the Key ID (kid) header because it's just the polite thing to do.
    // We only have one key in this example but a using a Key ID helps
    // facilitate a smooth key rollover process
    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

    // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

    // Sign the JWS and produce the compact serialization or the complete JWT/JWS
    // representation, which is a string consisting of three dot ('.') separated
    // base64url-encoded parts in the form Header.Payload.Signature
    // If you wanted to encrypt it, you can simply set this jwt as the payload
    // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
    String jwt = jws.getCompactSerialization();

    log.debug("Created jwt : ${jwt}")
    return jwt
  }

}
