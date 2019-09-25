package com.configurations;

import com.MainConfig;
import com.security.JwtUserAuthorisationFilter;
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

                .antMatchers("/swagger","/swagger.json","/swagger-static/*").permitAll()

                .antMatchers(HttpMethod.GET,"/users").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/users").permitAll()
                .antMatchers(HttpMethod.PUT,"/users","/users/*/books").authenticated()
                .antMatchers(HttpMethod.GET,"/users/*","/users/*/books").authenticated()
                .antMatchers(HttpMethod.DELETE,"/users/*/books").authenticated()
                .antMatchers(HttpMethod.DELETE,"/users/*").hasAnyRole("ADMIN")

                .antMatchers(HttpMethod.GET,"/books","/books/*").permitAll()
                .antMatchers(HttpMethod.POST,"/books").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PUT,"/books").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/books/*").hasAnyRole("ADMIN")

                .and()
                .addFilter(new JwtUserAuthorisationFilter(authenticationManager(),mainConfig))
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
