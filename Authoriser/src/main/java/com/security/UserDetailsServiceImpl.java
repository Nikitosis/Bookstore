package com.security;

import com.crossapi.dao.RoleDao;
import com.crossapi.dao.UserDao;
import com.crossapi.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDao userDao;

    private RoleDao roleDao;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userDao.findByUsername(username);


        if(user!=null){
            return buildUserDetails(user);
        }
        else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    private UserDetails buildUserDetails(User user){
        List<String> roles=roleDao.findByUser(user.getId()).stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        UserBuilder userBuilder=org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
        userBuilder.password(user.getPassword());
        userBuilder.roles(roles.toArray(new String[0]));

        return userBuilder.build();
    }
}
