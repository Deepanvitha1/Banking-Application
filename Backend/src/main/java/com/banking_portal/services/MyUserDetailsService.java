package com.banking_portal.services;

import com.banking_portal.exception.UserNotFoundException;
import com.banking_portal.models.UserPrincipal;
import com.banking_portal.constants.dto.UserEntity;
import com.banking_portal.facade.UserFacade;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserFacade userFacade;
    public MyUserDetailsService(UserFacade userFacade) {
        this.userFacade = userFacade;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userFacade.findByPhoneNumber(username).orElseThrow(()->new UserNotFoundException("User Not Found"));
        if(user == null) {
            throw new UserNotFoundException("user not found");
        }
        else {
            return new UserPrincipal(user);
        }
    }
}
