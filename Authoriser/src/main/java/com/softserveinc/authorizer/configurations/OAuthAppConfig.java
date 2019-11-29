package com.softserveinc.authorizer.configurations;

import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.cross_api_objects.security.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

@Configuration
public class OAuthAppConfig {
    @Autowired
    private MainConfig mainConfig;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(){
        return new InMemoryClientRegistrationRepository(googleClientRegistration(),facebookClientRegistration());
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    private ClientRegistration googleClientRegistration(){
        return CommonOAuth2Provider.GOOGLE.getBuilder(AuthProvider.google.toString())
                .clientId(mainConfig.getGoogleOAuth().getClientId())
                .clientSecret(mainConfig.getGoogleOAuth().getClientSecret())
                //we don't use default scopes, because it includes openid scope, which makes spring security
                //to use OidcUserService instead of DefaultOAuth2UserService. So our custom OAuth2UserService
                //is not called
                .scope(new String[]{"profile", "email"})
                .build();
    }
    private ClientRegistration facebookClientRegistration(){
        return CommonOAuth2Provider.FACEBOOK.getBuilder(AuthProvider.facebook.toString())
                .clientId(mainConfig.getFacebookOAuth().getClientId())
                .clientSecret(mainConfig.getFacebookOAuth().getClientSecret())
                .build();
    }

}
