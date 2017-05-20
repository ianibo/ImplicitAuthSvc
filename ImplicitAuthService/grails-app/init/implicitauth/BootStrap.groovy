package implicitauth

class BootStrap {

  def init = { servletContext ->
    log.debug("ImplicitAuth BootStrap::init");
  }

  def destroy = {
  }
}
