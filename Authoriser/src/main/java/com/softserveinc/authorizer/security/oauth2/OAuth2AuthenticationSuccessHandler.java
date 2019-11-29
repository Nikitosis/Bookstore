package com.softserveinc.authorizer.security.oauth2;

import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.authorizer.dao.RoleDao;
import com.softserveinc.authorizer.dao.UserDao;
import com.softserveinc.authorizer.security.oauth2.users.OAuth2UserInfo;
import com.softserveinc.authorizer.security.oauth2.users.OAuth2UserInfoFactory;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.security.AuthProvider;
import com.softserveinc.cross_api_objects.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Cookie;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private OAuth2TokenProvider oAuth2TokenProvider;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    private MainConfig mainConfig;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;

    public OAuth2AuthenticationSuccessHandler(){
        super();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2UserInfo oAuth2UserInfo=getOAuth2UserInfo(authentication);

        User curUser=userDao.findByEmail(oAuth2UserInfo.getEmail());

        String targetUrl= getTargetUrl(request,response,curUser);

        clearAuthenticationAttributes(request);

        response.sendRedirect(targetUrl);
    }

    public String getTargetUrl(HttpServletRequest request,HttpServletResponse response,User user){
        Optional<String> redirectUrl=CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(cookie -> cookie.getValue());

        //TODO: change this hardcoded url
        String targetUrl=redirectUrl.orElse("/defaultUrl");
        String token=mainConfig.getSecurity().getTokenPrefix()+oAuth2TokenProvider.createToken(user);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token",token)
                .build().toUriString();
    }

    private OAuth2UserInfo getOAuth2UserInfo(Authentication authentication){
        OAuth2AuthenticationToken authenticationToken=(OAuth2AuthenticationToken)authentication;
        String registrationId=((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        Map<String,Object> userAttributes=(Map<String,Object>)authenticationToken.getPrincipal().getAttributes();
        return OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                userAttributes
        );
    }
}
