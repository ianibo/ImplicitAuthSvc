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
