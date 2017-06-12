package implicitauth

/**
 *   Base class for User and Org - So either can be associated with actions and roles
 */ 
class Party {

  // def slugGeneratorService

  String uriName
  String displayName

  static mapping = {
    table name:'ia_party'
    tablePerHierarchy false
    id column:'p_id'
    version column:'p_version'
    uriName column:'p_uriname'
    displayName column:'p_displayname'
  }

  static hasMany = [
    incomingRelations:PartyRelationship,
    outgoingRelations:PartyRelationship
  ]

  static mappedBy = [
    incomingRelations:'to',
    outgoingRelations:'from'
  ]

  static constraints = {
        uriName (nullable:false, blank:false, maxSize:64)
    displayName (nullable:false, blank:false, maxSize:64)
  }

  // def beforeInsert() {
  //   this.slug = slugGeneratorService.generateSlug(this.class, "slug", displayName)
  // }

  // def beforeUpdate() {
  //   if (isDirty('name')) {
  //     this.slug = slugGeneratorService.generateSlug(this.class, "slug", displayName)
  //   }
  // }

}
