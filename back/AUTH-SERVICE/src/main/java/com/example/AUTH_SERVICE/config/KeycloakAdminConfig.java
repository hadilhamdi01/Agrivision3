package com.example.AUTH_SERVICE.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class KeycloakAdminConfig {


@Bean
public Keycloak keycloak() {
    return KeycloakBuilder.builder()
        .serverUrl("http://localhost:8085")
        .realm("master") 
        .clientId("admin-cli")
        .username("admin")
        .password("admin")
        .grantType("password")
        .build();
}

}
