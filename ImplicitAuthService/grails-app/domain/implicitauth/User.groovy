package implicitauth

import grails.plugin.springsecurity.SpringSecurityService
import grails.rest.Resource
import grails.util.Holders
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.util.Map;

import javax.persistence.Transient

@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User extends Party implements Serializable {

  private static final long serialVersionUID = 1

  transient springSecurityService

  String username
  String password
  boolean enabled = true
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired
  String localId

  String profilePic
  String email
  String biography
  String name

  Set socialIdentities = []
  
  static hasMany = [
    socialIdentities : SocialIdentity
  ]

  static mappedBy = [
    socialIdentities : 'user'
  ]

  User(String username, String password) {
    this()
    this.username = username
    this.password = password
  }
  
  Set<UserRole> getRoles() {
    UserRole.findAllByUser(this)
  }

  Set<Role> getAuthorities() {
    getRoles()*.role
  }
  
  boolean hasRole(String roleName) {
    Role r = Role.findByAuthority(roleName)
    if (r) {
      return UserRole.findByUserAndRole(this, r)
    }
    
    false
  }

  def beforeInsert() {
    if (password) {
      encodePassword()
    }
  }
  
  def beforeValidate() {
    if (!name) {
      name = username
    }
  }

  def beforeUpdate() {
    if (isDirty('password')) {
      encodePassword()
    }
  }

  protected void encodePassword() {
    password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
  }

  static transients = ['springSecurityService', 'verified', 'roles', 'authorities']

  static constraints = {
    username blank: false, unique: true, bindable: false
    password blank: false, bindable: false
    profilePic blank: true, nullable:true, bindable: false
    email blank: true, nullable:true
    name blank: true, nullable:true, bindable: true
    biography blank: true, nullable:true, bindable: false
    localId blank: true, nullable:true, bindable: false
    
    enabled bindable: false
    accountExpired bindable: false
    accountLocked bindable: false
    passwordExpired bindable: false
  
    socialIdentities bindable: false
  }

  static mapping = {
    table name:"ia_usr", schema:'authsvc'
    id: column: 'user_id'
    password column: 'user_pw'
  }
  
}
