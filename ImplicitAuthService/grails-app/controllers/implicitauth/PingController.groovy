package implicitauth

class PingController {

  def index() { 
    log.debug("PingController::index ${params} ${request.getHeader('Authorization')}");
  }
}
