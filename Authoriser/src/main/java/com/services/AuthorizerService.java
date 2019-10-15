package com.services;

import com.crossapi.models.User;
import com.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizerService {
    private UserDao userDao;

    @Autowired
    public AuthorizerService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void verifyEmail(String verificationToken){
        User user=userDao.findByVerificationToken(verificationToken);
        if(user!=null){
            user.setEmailVerified(true);
            userDao.update(user);
        }
    }
}
