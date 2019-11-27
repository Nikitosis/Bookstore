package com.softserveinc.authorizer.configurations;

import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.authorizer.security.JwtAuthenticationFilter;
import com.softserveinc.cross_api_objects.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityAppConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MainConfig mainConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserDao userDao;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                cors()
               .and()
               //.csrf().disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(),mainConfig,userDao))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                    .disable()
                .httpBasic()
                    .disable()
                .oauth2Login()
                    //specify loginPage to disable default oauth login Page
                    .loginPage("/someUrl");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH","OPTIONS"));
        configuration.addExposedHeader("Authorization");
        configuration.setAllowedHeaders(Arrays.asList("origin","content-type","accept","x-requested-with","Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
