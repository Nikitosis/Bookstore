package com.crossapi.security;

import com.okta.jwt.JoseException;
import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerifier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtServiceAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtServiceAuthorizationFilter.class);

    private JwtVerifier jwtVerifier;


    public JwtServiceAuthorizationFilter(AuthenticationManager authenticationManager, JwtVerifier jwtVerifier) {
        super(authenticationManager);
        this.jwtVerifier=jwtVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(CorsUtils.isPreFlightRequest(request)){
            chain.doFilter(request,response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken=getAuthentication(request);
        if(authenticationToken==null){
            chain.doFilter(request,response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request,response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        try{
            String token = request.getHeader("Authorization");
            if(StringUtils.isEmpty(token))
                return null;

            token=token.replace("Bearer ", "");

            Jwt jwt=jwtVerifier.decodeAccessToken(token);

            String username=(String)jwt.getClaims().get("sub");

            List<GrantedAuthority> authorities = ((List<String>) jwt.getClaims()
                    .get("scp")).stream()
                    .map(authority -> new SimpleGrantedAuthority(authority))
                    .collect(Collectors.toList());


            if (StringUtils.isNotEmpty(username)) {
                return new UsernamePasswordAuthenticationToken(username, null, authorities);
            }
        } catch (JoseException e) {
            log.warn("Failed to parse JWT for okta service");
        }
        return null;
    }
}
