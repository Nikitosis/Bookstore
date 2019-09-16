package com.security;

import com.MainConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sun.security.util.SecurityConstants;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private MainConfig mainConfig;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,MainConfig mainConfig) {
        this.authenticationManager = authenticationManager;
        this.mainConfig=mainConfig;

        setFilterProcessesUrl(mainConfig.getSecurity().getAuthenticationUrl());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username=request.getParameter("username");
        String password=request.getParameter("password");

        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(username,password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user=(User)authResult.getPrincipal();

        byte[] signingKey=mainConfig.getSecurity().getJwtSecret().getBytes();

        String token=Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setHeaderParam("typ",mainConfig.getSecurity().getTokenType())
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis()+864000000))
                .compact();

        response.addHeader(mainConfig.getSecurity().getTokenHeader(),
                mainConfig.getSecurity().getTokenPrefix()+token);
        System.out.println("Worked fine");
    }
}
