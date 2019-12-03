package com.softserveinc.authorizer;

import com.softserveinc.authorizer.configurations.OAuthConfig;
import com.softserveinc.cross_api_objects.configuration.SecurityConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private SecurityConfig security;

    private Long tokenExpirationTime;

    private String authenticationUrl;

    private OAuthConfig googleOAuth;

    private OAuthConfig facebookOAuth;

    public SecurityConfig getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfig security) {
        this.security = security;
    }

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public Long getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(Long tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public String getAuthenticationUrl() {
        return authenticationUrl;
    }

    public void setAuthenticationUrl(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }

    public OAuthConfig getGoogleOAuth() {
        return googleOAuth;
    }

    public void setGoogleOAuth(OAuthConfig googleOAuth) {
        this.googleOAuth = googleOAuth;
    }

    public OAuthConfig getFacebookOAuth() {
        return facebookOAuth;
    }

    public void setFacebookOAuth(OAuthConfig facebookOAuth) {
        this.facebookOAuth = facebookOAuth;
    }
}
