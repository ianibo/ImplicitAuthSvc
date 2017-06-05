package implicitauth

import implicitauth.OAuthAuthorizationService

import com.k_int.grails.tools.rules.RulesService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.SecurityFilterPosition

class BootStrap {

  def grailsApplication
  def authcfgService

  def init = { servletContext ->

    log.debug("ImplicitAuth BootStrap::init");

    SpringSecurityUtils.clientRegisterFilter('jwtPreauthFilter', SecurityFilterPosition.PRE_AUTH_FILTER)

    def role_user = Role.findByAuthority('ROLE_USER') ?: new Role(authority:'ROLE_USER').save(flush:true, failOnError:true)
    def role_ro_user = Role.findByAuthority('ROLE_RO_USER') ?: new Role(authority:'ROLE_RO_USER').save(flush:true, failOnError:true)
    def role_admin_user = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority:'ROLE_ADMIN').save(flush:true, failOnError:true)
    def role_verified_user = Role.findByAuthority('ROLE_VERIFIED_USER') ?: new Role(authority:'ROLE_VERIFIED_USER').save(flush:true, failOnError:true)
    def role_system = Role.findByAuthority('ROLE_SYSTEM') ?: new Role(authority:'ROLE_SYSTEM').save(flush:true, failOnError:true)

    def google_prov = OAuthAuthorizationService.findByCode('google') ?:
      new OAuthAuthorizationService(code:'google',
                                    baseUrl:'https://accounts.google.com/o/oauth2/v2/auth',
                                    scope:'https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/plus.login',
                                    redirectUrl:'http://localhost:8080/auth/oauth/google/callback',
                                    clientId:'452432600734-gg239i5odhfki1lhpt7c01pfir3267ol.apps.googleusercontent.com',
                                    responseType:'token').save(flush:true, failOnError:true);
  }

  def destroy = {
  }
}
