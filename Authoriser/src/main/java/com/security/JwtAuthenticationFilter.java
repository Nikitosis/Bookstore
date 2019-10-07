package com.security;

import com.MainConfig;
import com.crossapi.dao.UserDao;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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

    private AuthenticationManager authenticationManager;

    private MainConfig mainConfig;

    private UserDao userDao;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,MainConfig mainConfig,UserDao userDao) {
        this.authenticationManager = authenticationManager;
        this.mainConfig=mainConfig;
        this.userDao=userDao;

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

        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(username,password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user=(User)authResult.getPrincipal();

        List<String> roles=user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Long userId=userDao.findByUsername(user.getUsername()).getId();

        byte[] signingKey=mainConfig.getSecurity().getJwtSecret().getBytes();

        String token=Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setHeaderParam("typ",mainConfig.getSecurity().getTokenType())
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis()+mainConfig.getTokenExpirationTime()))
                .claim("roles",roles)
                .claim("userId",userId)
                .compact();

        response.addHeader(mainConfig.getSecurity().getTokenHeader(),
                mainConfig.getSecurity().getTokenPrefix()+token);
    }
}
