package com.softserveinc.feecharger;

import com.softserveinc.cross_api_objects.configuration.DependencyService;
import com.softserveinc.cross_api_objects.configuration.FeeChargeConfig;
import com.softserveinc.cross_api_objects.configuration.KafkaConfig;
import com.softserveinc.cross_api_objects.configuration.OktaOAuthConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private DependencyService libraryService;

    private DependencyService mailSenderService;

    private DependencyService loggerService;

    private FeeChargeConfig feeChargeConfig;

    private OktaOAuthConfig oktaOAuth;

    private KafkaConfig kafkaConfig;

    private String kafkaMailTopic;

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

    public DependencyService getMailSenderService() {
        return mailSenderService;
    }

    public void setMailSenderService(DependencyService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    public DependencyService getLoggerService() {
        return loggerService;
    }

    public void setLoggerService(DependencyService loggerService) {
        this.loggerService = loggerService;
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
}
