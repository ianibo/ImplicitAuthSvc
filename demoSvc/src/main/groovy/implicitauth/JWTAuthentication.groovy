package implicitauth

import org.springframework.security.core.*;
import org.jose4j.jwt.*
import org.springframework.security.core.authority.SimpleGrantedAuthority

public class JWTAuthentication implements Authentication {

    private static final long serialVersionUID = 1L;

    private String jwt=null;
    private boolean authenticated = false;
    private JwtClaims claims = null;
    private final Collection<GrantedAuthority> authorities;
    private User principal

    public JWTAuthentication(String jwt, JwtClaims claims, User principal) {
        this.jwt = jwt;
        this.claims = claims
        this.authenticated = false;

        List<GrantedAuthority> tmp = new ArrayList<>();
        if (principal?.authorities != null) {
          principal.authorities.each { role ->
            tmp.add(new SimpleGrantedAuthority(role.authority));
          }
        }
        this.authorities = Collections.unmodifiableList(tmp);
        this.principal = principal;
    }

    public String getJwt() {
        return jwt;
    }

    public JwtClaims getClaims() {
      return claims
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return claims.getSubject();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
    }

    @Override
    public Object getDetails() {
      return claims
    }

}
