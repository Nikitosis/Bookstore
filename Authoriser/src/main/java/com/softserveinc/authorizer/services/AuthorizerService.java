package com.softserveinc.authorizer.services;

import com.softserveinc.authorizer.dao.UserDao;
import com.softserveinc.cross_api_objects.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizerService {
    private UserDao userDao;

    @Autowired
    public AuthorizerService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void verifyEmail(String verificationToken) throws UsernameNotFoundException{
        User user=userDao.findByVerificationToken(verificationToken);

        if(user==null)
            throw new UsernameNotFoundException("User not found");

        if(user!=null){
            user.setEmailVerified(true);
            userDao.update(user);
        }
    }
}
