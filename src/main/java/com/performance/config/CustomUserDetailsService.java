package com.performance.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.performance.dao.UserRepository;
import com.performance.entities.User;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
    	System.out.println("User " + username + " not found in DB");
        User user = userRepository.findByUsername(username)
            .orElseThrow(() ->new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}

