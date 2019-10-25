package com.softserveinc.library;

import com.softserveinc.cross_api_objects.configuration.AwsConfig;
import com.softserveinc.cross_api_objects.configuration.DependencyService;
import com.softserveinc.cross_api_objects.configuration.OktaOAuthConfig;
import com.softserveinc.cross_api_objects.configuration.SecurityConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private DependencyService loggerService;

    private DependencyService feeChargerService;

    private String verificationUrl;

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

    public DependencyService getLoggerService() {
        return loggerService;
    }

    public void setLoggerService(DependencyService loggerService) {
        this.loggerService = loggerService;
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

    public String getVerificationUrl() {
        return verificationUrl;
    }

    public void setVerificationUrl(String verificationUrl) {
        this.verificationUrl = verificationUrl;
    }

    public DependencyService getMailSenderService() {
        return mailSenderService;
    }

    public void setMailSenderService(DependencyService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }
}
