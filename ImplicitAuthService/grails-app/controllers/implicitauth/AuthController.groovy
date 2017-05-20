package implicitauth

import grails.converters.JSON;

class AuthController {

  def index() { 
    log.debug("AuthController::index");
  }

  def authorize() {
    def response = [:]
    log.debug("AuthController::authorize() ${params}");
    render response as JSON
  }
}
