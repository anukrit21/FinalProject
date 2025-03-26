package com.demoApp.mess.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.demoApp.mess.entity.Mess;
import com.demoApp.mess.entity.Mess.Role;

import java.util.Collection;
import java.util.Collections;

public class MessUserDetails implements UserDetails {

    private final Mess mess;

    public MessUserDetails(Mess mess) {
        this.mess = mess;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ensure "ROLE_" prefix is added before role
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + mess.getRole()));
    }

    @Override
    public String getPassword() {
        return mess.getPassword();
    }

    @Override
    public String getUsername() {
        return mess.getEmail();
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
        return mess.isApproved();
    }

    public Long getId() {
        return mess.getId();
    }

    // Ensure the 'getRole' method exists in the Mess class
    public Role getRole() {
        return mess.getRole();  // This should be properly defined in the Mess class
    }
}
