package com;

import com.configuration.MailConfig;
import com.crossapi.configuration.OktaOAuthConfig;
import io.dropwizard.Configuration;

public class MainConfig extends Configuration {

    private MailConfig mailConfig;

    private OktaOAuthConfig oktaOAuth;

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
}
