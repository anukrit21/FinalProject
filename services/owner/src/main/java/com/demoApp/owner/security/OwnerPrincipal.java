package com.demoApp.owner.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.demoApp.owner.entity.Owner;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OwnerPrincipal implements UserDetails {

    private Long id;
    private String name;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public static OwnerPrincipal create(Owner owner) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_OWNER");
        
        return new OwnerPrincipal(
                owner.getId(),
                owner.getName(),
                owner.getEmail(),
                owner.getPassword(), 
                Collections.singletonList(authority)
        );
    }

    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public String getPassword() {
        return password;
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
        return true;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}