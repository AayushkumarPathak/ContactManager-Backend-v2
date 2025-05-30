package com.amz.scm.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.amz.scm.models.User;
import com.amz.scm.repositories.UserRepo;

@Service
public class CustomUserDetailService implements UserDetailsService {


    @Autowired
    private  UserRepo userRepo;

    @Override                             // username is email in this case
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    
    
        User user = this.userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return user;
    }
    
}
