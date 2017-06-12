package implicitauth.schema

class SocialIdentity {

  String provider
  String reference

  static belongsTo = [
    user : User
  ]

  static constraints = {
    provider blank:false, nullable:false
    reference blank: false, nullable:false
    user blank: false, nullable:false
  }

  static mapping = {
    table name:'ia_social_identity'
    user column: 'user_fk'
    provider column: 'provider'
    reference column: 'reference'
    password column: 'pwd'
  }

}
