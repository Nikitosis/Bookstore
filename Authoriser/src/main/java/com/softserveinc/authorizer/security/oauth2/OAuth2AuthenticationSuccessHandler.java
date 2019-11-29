package com.softserveinc.authorizer.security.oauth2;

import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.authorizer.dao.RoleDao;
import com.softserveinc.authorizer.dao.UserDao;
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
        OAuth2AuthenticationToken authenticationToken=(OAuth2AuthenticationToken)authentication;
        Map<String,Object> userAttributes=(Map<String,Object>)authenticationToken.getPrincipal().getAttributes();

        if(userDao.findByEmail((String)userAttributes.get("email"))==null){
            User user=new User();
            user.setfName((String)userAttributes.get("given_name"));
            user.setAvatarLink((String)userAttributes.get("picture"));
            user.setEmail((String)userAttributes.get("email"));
            user.setEmailVerified(true);
            user.setUsername((String)userAttributes.get("email"));
            user.setMoney(new BigDecimal("0"));
            user.setAuthProvider(AuthProvider.google);
            user.setSubscribedToNews(true);

            userDao.save(user);
            //setting role
            roleDao.addUserRole(user.getId(),"USER");
        }
        User curUser=userDao.findByEmail((String)userAttributes.get("email"));

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
}
