package com.softserveinc.library.services;

import com.softserveinc.library.MainConfig;
import com.amazonaws.services.codecommit.model.FileTooLargeException;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.softserveinc.cross_api_objects.dao.RoleDao;
import com.softserveinc.cross_api_objects.dao.UserDao;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.library.services.storage.AwsStorageService;
import com.softserveinc.library.services.storage.StoredFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {
    private UserDao userDao;
    private RoleDao roleDao;
    private AwsStorageService awsStorageService;
    private RequestSenderService requestSenderService;

    private PasswordEncoder passwordEncoder;

    private MainConfig mainConfig;

    @Autowired
    public UserService(UserDao userDao, RoleDao roleDao, AwsStorageService awsStorageService, RequestSenderService requestSenderService, PasswordEncoder passwordEncoder, MainConfig mainConfig) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.awsStorageService = awsStorageService;
        this.requestSenderService = requestSenderService;
        this.passwordEncoder = passwordEncoder;
        this.mainConfig = mainConfig;
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
        //setting password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //verifying email
        if(!StringUtils.isNullOrEmpty(user.getEmail()))
            resetVerification(user);

        //saving entity
        Long res=userDao.save(user);

        //setting role
        roleDao.addUserRole(user.getId(),"USER");

        return res;
    }

    public void setUserImage(User user, StoredFile file) throws IOException, IllegalArgumentException, FileTooLargeException {
        Long fileSize=getFileSize(file);
        if(fileSize<=4 || fileSize>mainConfig.getAwsS3Config().getMaxImageSize())
            throw new FileTooLargeException("Image size if too large or not defined. Max image size is "+mainConfig.getAwsS3Config().getMaxImageSize());


        if(!awsStorageService.isAllowedImageType(file.getFileName())){
            throw new IllegalArgumentException("Wrong image type");
        }

        String path=awsStorageService.uploadFile(file, CannedAccessControlList.PublicRead);
        String url=awsStorageService.getFileUrl(path);
        user.setAvatarLink(url);
        userDao.update(user);
    }

    public void update(User user){
        User originalUser=userDao.findById(user.getId());
        if(user.getPassword()!=null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(!Objects.equals(originalUser.getEmail(),user.getEmail()) && !StringUtils.isNullOrEmpty(user.getEmail())){
           resetVerification(user);
        }

        userDao.update(user);
    }

    public void delete(Long userId){
        userDao.delete(userId);
    }

    private void resetVerification(User user){
        String verificationToken=UUID.randomUUID().toString();
        String verificationUrl=mainConfig.getVerificationUrl()+"/"+verificationToken;
        Mail mail=new Mail(user.getEmail(),"Please,verify your email",
                "Email verification is required. Follow this link to verify your email: "+verificationUrl);
        requestSenderService.sendEmailVerification(mail);

        user.setVerificationToken(verificationToken);
        user.setEmailVerified(false);
    }

    private Long getFileSize(StoredFile storedFile){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(storedFile.getInputStream(),baos);
        } catch (IOException e) {
            e.printStackTrace();
            return -1L;
        }
        storedFile.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
        return Long.valueOf(baos.size());
    }
}
