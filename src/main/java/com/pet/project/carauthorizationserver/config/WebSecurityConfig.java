package com.pet.project.carauthorizationserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class WebSecurityConfig
        extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService uds() {
        InMemoryUserDetailsManager detailsManager = new InMemoryUserDetailsManager();

        UserDetails carUserDetails = User.withUsername("john")
                .password("12345")
                .authorities("write")
                .build();

        detailsManager.createUser(carUserDetails);
        UserDetails s3ToTopicUserDetails = User.withUsername("s3ToKafka")
                .password("12345")
                .authorities("write")
                .build();
        detailsManager.createUser(s3ToTopicUserDetails);
        return detailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
