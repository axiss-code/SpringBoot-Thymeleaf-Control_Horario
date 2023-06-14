package com.ilerna.wr.dto;

import com.ilerna.wr.entity.User;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/*
* Funcionalidad de Spring Security.
* El usuario que se autentica debe implementar la interfaz UserDetails para
* establecer los permisos asociados a su rol
*/
public class UserWrapper implements UserDetails{
    
    private User usrw;

    
    public UserWrapper(User user) {
        this.usrw = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(this.usrw.getRole().toString()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.usrw.getPassword();
    }

    @Override
    public String getUsername() {
        return this.usrw.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.usrw.getIsActive();
    }

}
