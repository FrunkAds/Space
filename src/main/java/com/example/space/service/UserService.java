package com.example.space.service;

import com.example.space.models.AuthenticationProvider;
import com.example.space.models.Role;
import com.example.space.models.User;
import com.example.space.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void createNewUserAfterOAuthLoginSuccess(String email, String name, String lastName, String firstName,
                                                    AuthenticationProvider provider) {
        User user = new User();
        user.setActive(true);
        user.setEmail(email);
        user.setLastName(lastName);
        user.setFirstName(firstName);
        user.setUsername(name);
        user.setFilename("unknow.png");
        user.setRoles(Collections.singleton(Role.USER));
        user.setAuthenticationProvider(provider);

        userRepository.save(user);
    }

    public void updateUserAfterOAuthLoginSuccess(User user,
                                                 String name,
                                                 AuthenticationProvider provider) {
        user.setFirstName(name);
        user.setAuthenticationProvider(provider);

        userRepository.save(user);
    }

    public boolean activeUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if(user==null){
            return false;
        }
        user.setActivationCode(null);
        userRepository.save(user);

        return true;
    }
}
