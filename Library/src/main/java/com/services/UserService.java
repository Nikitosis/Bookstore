package com.services;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.crossapi.dao.RoleDao;
import com.crossapi.dao.UserDao;
import com.crossapi.models.User;
import com.services.storage.AwsStorageService;
import com.services.storage.StoredFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
public class UserService {
    private UserDao userDao;
    private RoleDao roleDao;
    private AwsStorageService awsStorageService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao userDao, RoleDao roleDao, AwsStorageService awsStorageService, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.awsStorageService = awsStorageService;
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
        Long res=userDao.save(user);
        roleDao.addUserRole(user.getId(),"USER");
        return res;
    }

    public void setUserImage(User user, StoredFile file) throws IOException {
        String path=awsStorageService.uploadFile(file, CannedAccessControlList.PublicRead);
        URL url=awsStorageService.getFileUrl(path);
        user.setAvatarLink(url.toString());
        userDao.update(user);
    }

    public void update(User user){
        if(user.getPassword()!=null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.update(user);
    }

    public void delete(Long userId){
        userDao.delete(userId);
    }
}
