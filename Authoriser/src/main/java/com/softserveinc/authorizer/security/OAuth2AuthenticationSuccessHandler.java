package com.softserveinc.authorizer.security;

import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.authorizer.dao.UserDao;
import com.softserveinc.cross_api_objects.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private OAuth2TokenProvider oAuth2TokenProvider;
    @Autowired
    private MainConfig mainConfig;
    @Autowired
    private UserDao userDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authenticationToken=(OAuth2AuthenticationToken)authentication;
        Map<String,Object> userAttributes=(Map<String,Object>)authenticationToken.getPrincipal().getAttributes();
        User user=new User();
        user.setfName((String)userAttributes.get("given_name"));
        user.setAvatarLink((String)userAttributes.get("picture"));
        user.setEmail((String)userAttributes.get("email"));
        user.setEmailVerified(true);

        if(userDao.findByEmail(user.getEmail())==null){
            userDao.save(user);
        }

        String token=oAuth2TokenProvider.createToken(user);
        response.addHeader(mainConfig.getSecurity().getTokenHeader(),
                mainConfig.getSecurity().getTokenPrefix()+token);
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        //String token= OAuth2TokenProvider.createToken(authentication);
/*
        response.addHeader(mainConfig.getSecurity().getTokenHeader(),
                mainConfig.getSecurity().getTokenPrefix()+token);

        getRedirectStrategy().sendRedirect(request,response,"http://localhost:9004/targetUrl");
    */}
}
