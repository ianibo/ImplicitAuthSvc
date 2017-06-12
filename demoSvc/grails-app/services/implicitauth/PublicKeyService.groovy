package implicitauth

import org.apache.commons.codec.binary.Base64
import grails.core.GrailsApplication
import org.springframework.http.HttpStatus
import java.nio.charset.StandardCharsets
import grails.converters.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.GET
import org.jose4j.jwk.*
import org.jose4j.jwt.*
import org.jose4j.jwt.consumer.*
import org.jose4j.jws.*
import grails.transaction.Transactional

@Transactional
class PublicKeyService {

  private RsaJsonWebKey thekey = null;
  def grailsApplication
  def authcfgService

  public static final String AUTH_AUD='KIAuth'

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }

  def getAppPublicKey() {
    // See if the app has a public key, if not generate one and store it
    if ( thekey == null ) {
      log.debug("Creating public key");
      Map<String, Object> keyparams = authcfgService.cfg.kiauth_jwk
      thekey = new org.jose4j.jwk.RsaJsonWebKey(keyparams)
    }
    else {
      log.warn("No KEY");
    }

    return thekey;
  }


  def decodeJWT(jwt) {

    JwtClaims result = null

    def rsaJsonWebKey = getAppPublicKey();


    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            .setRequireExpirationTime() // the JWT must have an expiration time
            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
            .setRequireSubject() // the JWT must have a subject claim
            .setExpectedIssuer(AUTH_AUD)  // who creates the token and signs it
            .setExpectedAudience(AUTH_AUD) // to whom the token is intended to be sent
            .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
            .build(); // create the JwtConsumer instance

    try
    {
        //  Validate the JWT and process it to the Claims
        result = jwtConsumer.processToClaims(jwt);
        //log.debug("JWT validation succeeded! " + result);
    }
    catch (InvalidJwtException e)
    {
        // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
        // Hopefully with meaningful explanations(s) about what went wrong.
        log.error("Invalid JWT! " + e);
    }

    result
  }


}

