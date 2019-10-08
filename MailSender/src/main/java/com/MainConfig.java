package com;

import com.configuration.MailConfig;
import io.dropwizard.Configuration;

public class MainConfig extends Configuration {

    private MailConfig mailConfig;

    public MailConfig getMailConfig() {
        return mailConfig;
    }

    public void setMailConfig(MailConfig mailConfig) {
        this.mailConfig = mailConfig;
    }
}
