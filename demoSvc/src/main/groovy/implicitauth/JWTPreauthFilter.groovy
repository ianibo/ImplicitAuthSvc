package implicitauth

import groovy.util.logging.Log4j

import org.jose4j.jwt.*
import org.springframework.security.core.*
import org.springframework.security.core.context.SecurityContextHolder

@Log4j
public class JWTPreauthFilter extends org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter {

  def grailsApplication
  def publicKeyService

  def setPublicKeyService(publicKeyService) {
    this.publicKeyService = publicKeyService;
  }


  // @javax.annotation.PostConstruct
  def init() {
    log.debug("Init");
  }

  @Override
  def getPreAuthenticatedPrincipal(javax.servlet.http.HttpServletRequest request) {

    def result = null
  
    if ( publicKeyService == null ) {
      log.error("Unable to continue - no public key service");
      return null
    }

    try {
      log.debug("checking auth header");
      def authorization = request.getHeader("Authorization")
      if ( authorization ) {
        log.debug("Got auth header");
        def token = authorization.split(' ')[1];
  
        def payload = publicKeyService.decodeJWT(token)

        log.debug("Got payload ${payload} ${payload?.subject} from ${token}");

        if ( payload?.subject ) {

          log.debug("payload.subject present, attempting to locate user ${payload.subject}")

          log.debug("Got user subject:${payload.subject} cls:${payload.subject.class}")
          result = payload.subject


          // Doing this here instead of in the standard filter should enable @secured annotations
          if ( payload.subject ) {
            log.debug("Attempting to locate user ${payload.subject}")
            User.withTransaction() {
              def principal = User.findByUsername(payload.subject)
              log.debug("Got user ${principal}")
              org.springframework.security.core.Authentication auth = new JWTAuthentication(token,payload,principal);
              result = auth
            }
          }
        }
      }
      else {
        log.debug("No auth header present in request");
      }
    }
    catch ( Exception e ) {
      log.error("Problem in JWTPreauthFilter::getPreAuthenticatedCredentials",e);
    }
    finally {
    }

    result
  }


  @Override
  def getPreAuthenticatedCredentials(javax.servlet.http.HttpServletRequest request) {
    log.debug("getPreAuthenticatedCredentials....");
    return ""
  }

}
