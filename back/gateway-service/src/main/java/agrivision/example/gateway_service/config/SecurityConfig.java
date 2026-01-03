package agrivision.example.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {

    // 🔓 PUBLIC
    @Bean
    @Order(1)
    public SecurityWebFilterChain publicSecurityChain(ServerHttpSecurity http) {

        http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                "/auth/**",
                "/actuator/**"
            ))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex.anyExchange().permitAll());

        return http.build();
    }

    // 🔐 SECURED (JWT + ROLES)
    @Bean
    @Order(2)
    public SecurityWebFilterChain securedSecurityChain(ServerHttpSecurity http) {

        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                .pathMatchers("/history/**").hasAnyRole("USER", "ADMIN")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
            );

        return http.build();
    }

    // 🔑 CONVERTISSEUR JWT → ROLES KEYCLOAK
    private org.springframework.core.convert.converter.Converter<Jwt, Mono<AbstractAuthenticationToken>>
    jwtAuthConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("realm_access.roles");

        JwtAuthenticationConverter jwtConverter =
                new JwtAuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return jwt -> Mono.just(jwtConverter.convert(jwt));
    }
}
