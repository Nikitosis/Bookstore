package com.services;

import com.dao.UserDao;
import com.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserDao userDao;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll(){
        return userDao.findAll();
    }

    public User findByUsername(String username){
        return userDao.findByUsername(username);
    }

    public User findById(Long userId){
        return userDao.findById(userId);
    }

    public Long save(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.save(user);
    }

    public void update(User user){
        userDao.update(user);
    }

    public void delete(Long userId){
        userDao.delete(userId);
    }
}
