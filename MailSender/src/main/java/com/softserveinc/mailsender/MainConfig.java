package com.softserveinc.mailsender;

import com.softserveinc.cross_api_objects.configuration.KafkaConfig;
import com.softserveinc.mailsender.configuration.MailConfig;
import com.softserveinc.cross_api_objects.configuration.OktaOAuthConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class MainConfig extends Configuration {

    private MailConfig mailConfig;

    private OktaOAuthConfig oktaOAuth;

    private KafkaConfig kafkaConfig;

    private DataSourceFactory database;

    private String kafkaMailTopic;

    private String kafkaUserChangedEmailActionTopic;

    private String kafkaInvoiceActionTopic;

    public MailConfig getMailConfig() {
        return mailConfig;
    }

    public void setMailConfig(MailConfig mailConfig) {
        this.mailConfig = mailConfig;
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

    public String getKafkaUserChangedEmailActionTopic() {
        return kafkaUserChangedEmailActionTopic;
    }

    public void setKafkaUserChangedEmailActionTopic(String kafkaUserChangedEmailActionTopic) {
        this.kafkaUserChangedEmailActionTopic = kafkaUserChangedEmailActionTopic;
    }

    public String getKafkaInvoiceActionTopic() {
        return kafkaInvoiceActionTopic;
    }

    public void setKafkaInvoiceActionTopic(String kafkaInvoiceActionTopic) {
        this.kafkaInvoiceActionTopic = kafkaInvoiceActionTopic;
    }

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }
}
