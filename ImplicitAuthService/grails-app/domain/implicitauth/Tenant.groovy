package implicitauth

/**
 */ 

class Tenant extends Party implements Serializable {

  private static final long serialVersionUID = 1

  String notes

  static constraints = {
    notes nullable: true, blank: true, unique: true
  }


  static mapping = {
    table name:"ia_tenant"
    notes  column:'iat_notes'
  }

}
