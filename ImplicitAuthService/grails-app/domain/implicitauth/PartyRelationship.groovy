package implicitauth

/**
 *   Base class for rel
 *   For USERS and Orgs
 *   from:User to:Org role:UOAdmin|UOMember|UOReadOnly status: Requested|Approved|Declined
 *   For ORGS to ORGS
 *   for CONSORTIA to ORGS
 *   from:Member to:Consortia role:ConsMember status: Requested|Approved|Declined
 * 
 */ 
class PartyRelationship {

  // Source of relationship
  Party from

  // Destination
  Party to

  // RDC -- relationshipRole - UOAdmin UOMember UOReadOnly
  String role

  // RDC -- relationshipStatus - Requested|Approved|Declined
  String status

  static mapping = {
    table name:'ia_party_relationship'
    id column:'pr_id'
    version column:'pr_version'
    from column:'p_from'
    to column:'p_to'
    role column:'p_role'
    status column:'p_status'
  }

  static constraints = {
        from (nullable:false, blank:false, maxSize:64)
          to (nullable:false, blank:false, maxSize:64)
        role (nullable:false, blank:false, maxSize:64)
      status (nullable:false, blank:false, maxSize:64)
  }

}
