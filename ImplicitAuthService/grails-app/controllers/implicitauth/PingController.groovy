package implicitauth

import grails.converters.JSON;

class PingController {

  def index() { 
    def result = [ 'response' : 'ok' ]
    log.debug("PingController::index ${params} ${request.getHeader('Authorization')}");
    render result as JSON
  }
}
