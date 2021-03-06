package com.softserveinc.feecharger;

import com.softserveinc.cross_api_objects.configuration.*;
import com.softserveinc.feecharger.configuration.FeeChargeConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private DependencyService libraryService;

    private FeeChargeConfig feeChargeConfig;

    private OktaOAuthConfig oktaOAuth;

    private KafkaConfig kafkaConfig;

    private String kafkaMailTopic;

    private String kafkaUserBookActionTopic;

    private String kafkaUserBookExtendActionTopic;

    private String kafkaUserBookPaymentActionTopic;

    private AwsS3Config awsS3Config;

    private String invoiceLambdaGeneratorUrl;

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public DependencyService getLibraryService() {
        return libraryService;
    }

    public void setLibraryService(DependencyService libraryService) {
        this.libraryService = libraryService;
    }

    public FeeChargeConfig getFeeChargeConfig() {
        return feeChargeConfig;
    }

    public void setFeeChargeConfig(FeeChargeConfig feeChargeConfig) {
        this.feeChargeConfig = feeChargeConfig;
    }

    public OktaOAuthConfig getOktaOAuth() {
        return oktaOAuth;
    }

    public void setOktaOAuth(OktaOAuthConfig oktaOAuth) {
        this.oktaOAuth = oktaOAuth;
    }

    public KafkaConfig getKafkaConfig() {
        return kafkaConfig;
    }

    public void setKafkaConfig(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    public String getKafkaMailTopic() {
        return kafkaMailTopic;
    }

    public void setKafkaMailTopic(String kafkaMailTopic) {
        this.kafkaMailTopic = kafkaMailTopic;
    }

    public String getKafkaUserBookActionTopic() {
        return kafkaUserBookActionTopic;
    }

    public void setKafkaUserBookActionTopic(String kafkaUserBookActionTopic) {
        this.kafkaUserBookActionTopic = kafkaUserBookActionTopic;
    }

    public String getKafkaUserBookExtendActionTopic() {
        return kafkaUserBookExtendActionTopic;
    }

    public void setKafkaUserBookExtendActionTopic(String kafkaUserBookExtendActionTopic) {
        this.kafkaUserBookExtendActionTopic = kafkaUserBookExtendActionTopic;
    }

    public String getKafkaUserBookPaymentActionTopic() {
        return kafkaUserBookPaymentActionTopic;
    }

    public void setKafkaUserBookPaymentActionTopic(String kafkaUserBookPaymentActionTopic) {
        this.kafkaUserBookPaymentActionTopic = kafkaUserBookPaymentActionTopic;
    }

    public AwsS3Config getAwsS3Config() {
        return awsS3Config;
    }

    public void setAwsS3Config(AwsS3Config awsS3Config) {
        this.awsS3Config = awsS3Config;
    }

    public String getInvoiceLambdaGeneratorUrl() {
        return invoiceLambdaGeneratorUrl;
    }

    public void setInvoiceLambdaGeneratorUrl(String invoiceLambdaGeneratorUrl) {
        this.invoiceLambdaGeneratorUrl = invoiceLambdaGeneratorUrl;
    }
}
