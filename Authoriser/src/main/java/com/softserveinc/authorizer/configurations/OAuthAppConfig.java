package com.softserveinc.authorizer.configurations;

import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.authorizer.security.oauth2.OAuth2UserService;
import com.softserveinc.cross_api_objects.security.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

@Configuration
public class OAuthAppConfig {
    @Autowired
    private MainConfig mainConfig;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(){
        return new InMemoryClientRegistrationRepository(googleClientRegistration());
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
                .clientId(mainConfig.getGoogleOauth().getClientId())
                .clientSecret(mainConfig.getGoogleOauth().getClientSecret())
                //we don't use default scopes, because it includes openid scope, which makes spring security
                //to use OidcUserService instead of DefaultOAuth2UserService. So our custom OAuth2UserService
                //is not called
                .scope(new String[]{"profile", "email"})
                .build();
    }
}
