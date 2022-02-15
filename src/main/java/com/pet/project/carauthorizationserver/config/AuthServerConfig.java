package com.pet.project.carauthorizationserver.config;

import org.springframework.beans.factory.annotation.Autowired;
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
public class AuthServerConfig
        extends AuthorizationServerConfigurerAdapter {

    @Autowired
    public AuthenticationManager authenticationManager;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                //в проде так не делать, т.к такой грант тайп предполает, что логин/пароль пользователя
                // видит client, а это не безопасно
                .withClient("rentCarRequest")
                .secret("rentCarRequestSecret")
                .authorizedGrantTypes("password")
                .scopes("write");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(converter());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("isAuthenticated()"); // isAuthenticated() permitAll()
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
                        new ClassPathResource("cars.jks"),
                        "cars123".toCharArray()
                );

        conv.setKeyPair(keyFactory.getKeyPair("cars"));

        return conv;
    }
}
