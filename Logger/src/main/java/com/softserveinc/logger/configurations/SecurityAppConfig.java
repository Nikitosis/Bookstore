package com.softserveinc.logger.configurations;

import com.softserveinc.logger.MainConfig;
import com.softserveinc.cross_api_objects.configuration.OktaOAuthConfig;
import com.softserveinc.cross_api_objects.security.JwtServiceAuthorizationFilter;
import com.softserveinc.cross_api_objects.security.JwtUserAuthorizationFilter;
import com.nimbusds.oauth2.sdk.ParseException;
import com.okta.jwt.JwtHelper;
import com.okta.jwt.JwtVerifier;
import liquibase.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityAppConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MainConfig mainConfig;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtVerifier jwtVerifier() throws IOException, ParseException {
        OktaOAuthConfig oAuthConfig=mainConfig.getOktaOAuth();

        JwtHelper helper=new JwtHelper()
                .setIssuerUrl(oAuthConfig.getIssuer());

        String audience=oAuthConfig.getAudience();
        if(StringUtils.isNotEmpty(audience)){
            helper.setAudience(audience);
        }

        return helper.build();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //create blank UserDetailsService, otherwise spring security will throw exception
        auth.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
                return null;
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/actions").hasAnyAuthority("write")
                .antMatchers("/actions").hasAnyRole("ADMIN")
                .and()
                .addFilter(new JwtServiceAuthorizationFilter(authenticationManager(),jwtVerifier()))
                .addFilter(new JwtUserAuthorizationFilter(authenticationManager(),mainConfig.getSecurity()))
                //we don't save user's session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
                .and()
                .csrf().disable();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("origin","content-type","accept","x-requested-with","Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
