package com.softserveinc.authorizer.security.oauth2;

import com.softserveinc.authorizer.dao.RoleDao;
import com.softserveinc.authorizer.dao.UserDao;
import com.softserveinc.authorizer.security.oauth2.exceptions.OAuth2AuthenticationProcessingException;
import com.softserveinc.authorizer.security.oauth2.users.OAuth2UserInfo;
import com.softserveinc.authorizer.security.oauth2.users.OAuth2UserInfoFactory;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.security.AuthProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.attribute.UserPrincipal;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User=super.loadUser(userRequest);

        try{
            return processOAuth2User(userRequest,oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }

    }

    //if user doesn't exists, then create user
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest,OAuth2User oAuth2User){
        OAuth2UserInfo oAuth2UserInfo=OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes()
        );

        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())){
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        User user=userDao.findByEmail(oAuth2UserInfo.getEmail());
        //if user already exists
        if(user!=null){
            //check if user wants to login not via his initial oauth provider
            if(!user.getAuthProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))){
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with "+
                user.getAuthProvider()+" account. Please use your "+user.getAuthProvider()+" account to login.");
            }
        }
        else{
            user=registerNewUser(oAuth2UserRequest,oAuth2UserInfo);
        }

        return oAuth2User;
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest,OAuth2UserInfo oAuth2UserInfo){
        User user=new User();

        user.setfName(oAuth2UserInfo.getName());
        user.setAvatarLink(oAuth2UserInfo.getImageUrl());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setEmailVerified(true);
        user.setUsername(oAuth2UserInfo.getEmail());
        user.setMoney(new BigDecimal("0"));
        user.setAuthProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setSubscribedToNews(true);

        userDao.save(user);

        roleDao.addUserRole(user.getId(),"USER");

        return user;
    }
}
