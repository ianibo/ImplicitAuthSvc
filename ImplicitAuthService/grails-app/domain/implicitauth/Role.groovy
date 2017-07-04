package implicitauth

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

class Role implements Serializable {

  private static final long serialVersionUID = 1

  String authority

  // Role(String authority) {
  //   this()
  //   this.authority = authority
  // }

  static constraints = {
    authority blank: false, unique: true
  }

  static mapping = {
    table name:'ia_role'
    authority: column:'role_auth'
    cache true
  }

}
