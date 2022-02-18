package com.pet.project.carauthorizationserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final String KEY_PAIR_ALIAS = "cars";
    private static final String KEY_PAIR_CLASS_PATH = "cars.jks";
    private static final String PASSWORD_GRANT_TYPE = "password";
    private static final String RENT_CAR_CLIENT = "rentCarClient";
    private static final String S3_TO_KAFKA_CLIENT = "s3ToKafkaClient";
    private static final String TOKEN_KEY_ACCESS = "isAuthenticated()";
    private static final String WRITE_SCOPE = "write";

    @Value("${car-authorization-server.key-pair-password}")
    private String keyPairPassword;

    @Value("${car-authorization-server.applications.rent-car.client-secret}")
    private String rentCarSecret;

    @Value("${car-authorization-server.applications.s3-to-kafka.client-secret}")
    private String s3ToKafkaSecret;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(RENT_CAR_CLIENT)
                .secret(rentCarSecret)
                .authorizedGrantTypes(PASSWORD_GRANT_TYPE)
                .scopes(WRITE_SCOPE)
                .and()
                .withClient(S3_TO_KAFKA_CLIENT)
                .secret(s3ToKafkaSecret)
                .authorizedGrantTypes(PASSWORD_GRANT_TYPE)
                .scopes(WRITE_SCOPE);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(converter());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess(TOKEN_KEY_ACCESS);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(converter());
    }

    @Bean
    public JwtAccessTokenConverter converter() {
        var conv = new JwtAccessTokenConverter();

        KeyStoreKeyFactory keyFactory =
                new KeyStoreKeyFactory(
                        new ClassPathResource(KEY_PAIR_CLASS_PATH),
                        keyPairPassword.toCharArray()
                );

        conv.setKeyPair(keyFactory.getKeyPair(KEY_PAIR_ALIAS));

        return conv;
    }
}
