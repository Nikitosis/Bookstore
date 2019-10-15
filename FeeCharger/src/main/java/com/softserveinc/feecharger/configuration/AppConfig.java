package com.softserveinc.feecharger.configuration;

import com.softserveinc.feecharger.MainConfig;
import com.softserveinc.cross_api_objects.services.OktaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com")
public class AppConfig {

    private MainConfig mainConfig;

    @Autowired
    public AppConfig(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

    @Bean
    public OktaService oktaService(){
        return new OktaService(mainConfig.getOktaOAuth());
    }
}
