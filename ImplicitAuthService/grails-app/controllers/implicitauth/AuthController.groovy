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

  def callback() {
    log.debug("AuthController::callback() ${params}");
    def response = [:]

    if ( ( params.provider?.length() > 0 )  && ( params.access_token?.length() > 0 ) ) {
      def authorization_svc = OAuthAuthorizationService.findByCode(params.provider)
      if ( authorization_svc != null ) {
        def token_result = exchangeAuthCodeForToken(params.access_token,authorization_svc)
      }
    }

    // send back a redirect containing auth_token param which contains the JWT
    render response as JSON
  }

  private def exchangeAuthCodeForToken(String token, provider_cfg) {
    log.debug("exchangeAuthCodeForToken(${token},${provider_cfg}");
  }
}
