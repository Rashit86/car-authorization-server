package com.pet.project.carauthorizationserver.config;

import org.springframework.beans.factory.annotation.Value;
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
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String RENT_CAR_USER_NAME = "john";
    private static final String S3_TO_KAFKA_USER_NAME = "s3ToKafka";
    private static final String WRITE_SCOPE = "write";

    @Value("${car-authorization-server.applications.rent-car.user-password}")
    private String rentCarUserPassword;

    @Value("${car-authorization-server.applications.s3-to-kafka.user-password}")
    private String s3ToKafkaUserPassword;

    @Bean
    public UserDetailsService uds() {
        InMemoryUserDetailsManager detailsManager = new InMemoryUserDetailsManager();

        UserDetails carUserDetails = User.withUsername(RENT_CAR_USER_NAME)
                .password(rentCarUserPassword)
                .authorities(WRITE_SCOPE)
                .build();

        detailsManager.createUser(carUserDetails);
        UserDetails s3ToTopicUserDetails = User.withUsername(S3_TO_KAFKA_USER_NAME)
                .password(s3ToKafkaUserPassword)
                .authorities(WRITE_SCOPE)
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
