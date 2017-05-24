package implicitauth

import grails.converters.JSON;

class AuthController {

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
                '&response_type='+java.net.URLEncoder.encode('token','UTF-8')+
                '&client_id='+java.net.URLEncoder.encode(authorization_svc.clientId,'UTF-8')
              );
        // redirect(url:'https://accounts.google.com/o/oauth2/v2/auth'+
        //         '?scope='+java.net.URLEncoder.encode('https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile','UTF-8')+
        //         '&redirect_uri='+java.net.URLEncoder.encode('http://localhost:8080/authorize/google','UTF-8')+
        //         '&response_type='+java.net.URLEncoder.encode('token','UTF-8')+
        //         '&client_id='+java.net.URLEncoder.encode('452432600734-gg239i5odhfki1lhpt7c01pfir3267ol.apps.googleusercontent.com','UTF-8')
      }
      else {
        log.error("Unable to locate provider");
      }
    }
  }

  def authorize() {
    def response = [:]
    log.debug("AuthController::authorize() ${params}");
    render response as JSON
  }
}
