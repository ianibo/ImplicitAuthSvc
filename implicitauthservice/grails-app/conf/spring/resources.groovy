// Place your Spring DSL code here
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.web.databinding.DataBindingUtils

beans = {

    userDetailsService(GormUserDetailsService) {
      grailsApplication = ref('grailsApplication')
    }

    userDetailsByNameServiceWrapper(org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper) {
      userDetailsService = ref('userDetailsService')
    }

    preAuthenticatedAuthenticationProvider(org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider) {
      preAuthenticatedUserDetailsService = ref('userDetailsByNameServiceWrapper')
    }

    // authEventHandler(AuthEventHandler) {
    // }

    jwtPreauthFilter(implicitauth.JWTPreauthFilter){
      grailsApplication = ref('grailsApplication')
      authenticationManager = ref('authenticationManager')
      publicKeyService = ref('publicKeyService')
    }

    // Change the authentication entry point so a 401 is sent instead of a redirect.
    authenticationEntryPoint(org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint,  'JWT: realm="Implicit Auth Svc"')

}

