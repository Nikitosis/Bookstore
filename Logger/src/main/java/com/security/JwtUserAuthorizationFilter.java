package com.security;

import com.MainConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import liquibase.util.StringUtils;
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

public class JwtUserAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtUserAuthorizationFilter.class);

    private MainConfig mainConfig;

    public JwtUserAuthorizationFilter(AuthenticationManager authenticationManager, MainConfig mainConfig) {
        super(authenticationManager);
        this.mainConfig = mainConfig;
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
        String token = request.getHeader(mainConfig.getSecurity().getTokenHeader());
        if (StringUtils.isNotEmpty(token) && token.startsWith(mainConfig.getSecurity().getTokenPrefix())) {

            try {
                String signingKey = mainConfig.getSecurity().getJwtSecret();

                //get rid of prefix
                token=token.replace(mainConfig.getSecurity().getTokenPrefix(), "");

                Jws<Claims> parsedToken = Jwts.parser()
                        .setSigningKey(signingKey.getBytes())
                        .parseClaimsJws(token);

                String username = parsedToken
                        .getBody()
                        .getSubject();

                List<GrantedAuthority> authorities = ((List<String>) parsedToken.getBody()
                        .get("roles")).stream()
                        .map(authority -> new SimpleGrantedAuthority(authority))
                        .collect(Collectors.toList());

                if (StringUtils.isNotEmpty(username)) {
                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                }
            } catch (ExpiredJwtException exception) {
                log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            } catch (MalformedJwtException exception) {
                log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            } catch (SignatureException exception) {
                log.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
            } catch (IllegalArgumentException exception) {
                log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
            }
        }
        return null;
    }
}
