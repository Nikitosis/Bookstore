package com.softserveinc.library;

import com.softserveinc.cross_api_objects.configuration.*;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private String verificationUrl;

    private SecurityConfig security;

    private OktaOAuthConfig oktaOAuth;

    private SwaggerBundleConfiguration swagger;

    private AwsS3Config awsS3Config;

    private KafkaConfig kafkaConfig;

    private String kafkaUserBookActionTopic;

    private String kafkaUserChangedEmailActionTopic;

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public String getVerificationUrl() {
        return verificationUrl;
    }

    public void setVerificationUrl(String verificationUrl) {
        this.verificationUrl = verificationUrl;
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

    public AwsS3Config getAwsS3Config() {
        return awsS3Config;
    }

    public void setAwsS3Config(AwsS3Config awsS3Config) {
        this.awsS3Config = awsS3Config;
    }

    public KafkaConfig getKafkaConfig() {
        return kafkaConfig;
    }

    public void setKafkaConfig(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    public String getKafkaUserBookActionTopic() {
        return kafkaUserBookActionTopic;
    }

    public void setKafkaUserBookActionTopic(String kafkaUserBookActionTopic) {
        this.kafkaUserBookActionTopic = kafkaUserBookActionTopic;
    }

    public String getKafkaUserChangedEmailActionTopic() {
        return kafkaUserChangedEmailActionTopic;
    }

    public void setKafkaUserChangedEmailActionTopic(String kafkaUserChangedEmailActionTopic) {
        this.kafkaUserChangedEmailActionTopic = kafkaUserChangedEmailActionTopic;
    }
}
