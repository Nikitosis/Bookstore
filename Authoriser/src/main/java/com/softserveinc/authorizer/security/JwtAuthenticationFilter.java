package com.softserveinc.authorizer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.cross_api_objects.dao.UserDao;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger log= LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;

    private MainConfig mainConfig;

    private UserDao userDao;

    private TokenProvider tokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,MainConfig mainConfig,UserDao userDao,TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.mainConfig=mainConfig;
        this.userDao=userDao;
        this.tokenProvider=tokenProvider;

        setFilterProcessesUrl(mainConfig.getAuthenticationUrl());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //if it is preflight request
        if(CorsUtils.isPreFlightRequest(request)){
            response.setStatus(HttpServletResponse.SC_OK);
            return null;
        }

        String username=request.getParameter("username");
        String password=request.getParameter("password");

        log.info("Trying to authenticate user");

        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(username,password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User authUser=(User)authResult.getPrincipal();
        com.softserveinc.cross_api_objects.models.User user=userDao.findByUsername(authUser.getUsername());

        String token=tokenProvider.createToken(user);

        response.addHeader(mainConfig.getSecurity().getTokenHeader(),
                mainConfig.getSecurity().getTokenPrefix()+token);

        writeUserToResponse(user.getId(),response);

        log.info("User successfully authenticated");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);

        log.info("User not authenticated");
    }

    private void writeUserToResponse(Long userId, HttpServletResponse response) throws IOException {
        com.softserveinc.cross_api_objects.models.User user=userDao.findById(userId);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(
                new ObjectMapper().writeValueAsString(user)
        );
    }
}
