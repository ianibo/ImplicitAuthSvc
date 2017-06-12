package implicitauth

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.SecurityFilterPosition
import implicitauth.schema.*;


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

    // For "god" users
    def role_global_adm = Role.findByAuthority('ROLE_GLOBAL_ADM') ?: new Role(authority:'ROLE_GLOBAL_ADM').save(flush:true, failOnError:true)

    // Pseudo role for system agents -- not for real people
    def role_system = Role.findByAuthority('ROLE_SYSTEM') ?: new Role(authority:'ROLE_SYSTEM').save(flush:true, failOnError:true)



    // authcfgService will expect there to be a yml config file somewhere like /etc/kiauthcfg.yml we can use the contents of that file to bootstrap
    // the db records for auth services.

    log.debug("Located auth services:");
    authcfgService.getCfg().auth.each { svcdef ->
      log.debug("Auth svc ${svcdef}");
    }

    def google_prov = OAuthAuthorizationSvcDefn.findByCode('google') ?:
      new OAuthAuthorizationSvcDefn(code:'google',
                                    baseUrl:'https://accounts.google.com/o/oauth2/v2/auth',
                                    scope:'https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/plus.login',
                                    redirectUrl:'http://localhost:8080/auth/oauth/google/callback',
                                    clientId:'452432600734-gg239i5odhfki1lhpt7c01pfir3267ol.apps.googleusercontent.com',
                                    responseType:'token').save(flush:true, failOnError:true);

    Role.list().each { role ->
      log.debug("Role: ${role}");
    }
  }

  def destroy = {
  }
}
