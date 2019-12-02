package com.softserveinc.authorizer.security.oauth2.users;

import com.softserveinc.authorizer.security.oauth2.exceptions.OAuth2AuthenticationProcessingException;
import com.softserveinc.cross_api_objects.security.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,Map<String,Object> attributes){
        if(registrationId.equalsIgnoreCase(AuthProvider.google.toString())){
            return new GoogleOAuth2UserInfo(attributes);
        }
        if (registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())){
            return new FacebookOAuth2UserInfo(attributes);
        }

        throw new OAuth2AuthenticationProcessingException("Sorry, but login with "+registrationId+" is not supported yet");
    }
}
