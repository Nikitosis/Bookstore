package com.configuration;

import com.MainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
@ComponentScan("com")
public class AppConfig {
    private MainConfig mainConfig;

    @Autowired
    public AppConfig(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

    @Bean
    public Session getMailSession(){

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return Session.getInstance(prop,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mainConfig.getMailConfig().getUsername(),mainConfig.getMailConfig().getPassword());
                    }
                });
    }
}
