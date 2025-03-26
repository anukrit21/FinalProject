package com.demoApp.owner.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.demoApp.owner.entity.Owner;
import com.demoApp.owner.repository.OwnerRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final OwnerRepository ownerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Owner owner = ownerRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Owner not found with email: " + username));

        return new User(owner.getEmail(),
                owner.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + owner.getRole())));
    }
}