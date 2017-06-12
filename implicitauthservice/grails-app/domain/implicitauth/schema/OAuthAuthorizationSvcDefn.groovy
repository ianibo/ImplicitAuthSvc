package implicitauth.schema

class OAuthAuthorizationSvcDefn {

  static mapping = {
    table name:"ia_auth_service"
  }

  String code
  String baseUrl
  String scope
  String redirectUrl
  String clientId
  String responseType

  static constraints = {
  }
}
