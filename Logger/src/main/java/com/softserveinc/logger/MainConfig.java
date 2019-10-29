package com.softserveinc.logger;

import com.softserveinc.cross_api_objects.configuration.OktaOAuthConfig;
import com.softserveinc.cross_api_objects.configuration.SecurityConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private OktaOAuthConfig oktaOAuth;

    private SecurityConfig security;

    private SwaggerBundleConfiguration swagger;

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public OktaOAuthConfig getOktaOAuth() {
        return oktaOAuth;
    }

    public void setOktaOAuth(OktaOAuthConfig oktaOAuth) {
        this.oktaOAuth = oktaOAuth;
    }

    public SecurityConfig getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfig security) {
        this.security = security;
    }

    public SwaggerBundleConfiguration getSwagger() {
        return swagger;
    }

    public void setSwagger(SwaggerBundleConfiguration swagger) {
        this.swagger = swagger;
    }
}