package implicitauth

class BootStrap {

  def init = { servletContext ->
    log.debug("ImplicitAuth BootStrap::init");

    def google_prov = OAuthAuthorizationService.findByCode('google') ?:
      new OAuthAuthorizationService.findByCode(code:'google',
                                               baseUrl:'https://accounts.google.com/o/oauth2/v2/auth',
                                               scope:'https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile',
                                               redirectUrl:'http://localhost:8080/authorize/google',
                                               clientId:'452432600734-gg239i5odhfki1lhpt7c01pfir3267ol.apps.googleusercontent.com',
                                               responseType:).save(flush:true, failOnError:true);
  }

  def destroy = {
  }
}
