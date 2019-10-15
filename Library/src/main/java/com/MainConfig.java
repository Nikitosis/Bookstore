package com;

import com.crossapi.configuration.AwsConfig;
import com.crossapi.configuration.DependencyService;
import com.crossapi.configuration.OktaOAuthConfig;
import com.crossapi.configuration.SecurityConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private DependencyService clientBookLoggerService;

    private DependencyService feeChargerService;

    private DependencyService authorizerService;

    private DependencyService mailSenderService;

    private SecurityConfig security;

    private OktaOAuthConfig oktaOAuth;

    private SwaggerBundleConfiguration swagger;

    private AwsConfig awsS3Config;

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public DependencyService getClientBookLoggerService() {
        return clientBookLoggerService;
    }

    public void setClientBookLoggerService(DependencyService clientBookLoggerService) {
        this.clientBookLoggerService = clientBookLoggerService;
    }

    public DependencyService getFeeChargerService() {
        return feeChargerService;
    }

    public void setFeeChargerService(DependencyService feeChargerService) {
        this.feeChargerService = feeChargerService;
    }

    public SecurityConfig getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfig security) {
        this.security = security;
    }

    public OktaOAuthConfig getOktaOAuth() {
        return oktaOAuth;
    }

    public void setOktaOAuth(OktaOAuthConfig oktaOAuth) {
        this.oktaOAuth = oktaOAuth;
    }

    public SwaggerBundleConfiguration getSwagger() {
        return swagger;
    }

    public void setSwagger(SwaggerBundleConfiguration swagger) {
        this.swagger = swagger;
    }

    public AwsConfig getAwsS3Config() {
        return awsS3Config;
    }

    public void setAwsS3Config(AwsConfig awsS3Config) {
        this.awsS3Config = awsS3Config;
    }

    public DependencyService getAuthorizerService() {
        return authorizerService;
    }

    public void setAuthorizerService(DependencyService authorizerService) {
        this.authorizerService = authorizerService;
    }

    public DependencyService getMailSenderService() {
        return mailSenderService;
    }

    public void setMailSenderService(DependencyService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }
}
