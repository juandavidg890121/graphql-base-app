package aem.example.springboot.graphqlbaseapp.infrastructure.config.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import aem.example.springboot.graphqlbaseapp.infrastructure.service.security.TokenStore;

public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private TokenProvider tokenProvider;
    private TokenStore tokenStore;

    public JWTConfigurer(TokenProvider tokenProvider, TokenStore tokenStore) {
        this.tokenProvider = tokenProvider;
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JWTFilter customFilter = new JWTFilter(tokenProvider, tokenStore);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}