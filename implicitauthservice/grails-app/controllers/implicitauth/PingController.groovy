package implicitauth

import grails.converters.JSON;
import grails.plugin.springsecurity.annotation.Secured


class PingController {

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def result = [ 'response' : 'ok' ]
    log.debug("PingController::index ${params} ${request.getHeader('Authorization')}");
    render result as JSON
  }
}
