package com.softserveinc.authorizer.security;

import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.authorizer.dao.UserDao;
import com.softserveinc.cross_api_objects.models.Role;
import com.softserveinc.cross_api_objects.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OAuth2TokenProvider {
    @Autowired
    private UserDao userDao;
    @Autowired
    private MainConfig mainConfig;

    public String createToken(User user){

        Long userId=userDao.findByEmail(user.getEmail()).getId();
        List<String> roles=userDao.findRolesByUser(userId)
                .stream()
                .map(role -> "ROLE_"+role.getName())
                .collect(Collectors.toList());


        byte[] signingKey=mainConfig.getSecurity().getJwtSecret().getBytes();

        String token=Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setHeaderParam("typ",mainConfig.getSecurity().getTokenType())
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis()+mainConfig.getTokenExpirationTime()))
                .claim("roles",roles)
                .claim("userId",userId)
                .compact();

        return token;
    }
}
