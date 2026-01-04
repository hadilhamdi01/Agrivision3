package agrivision.example.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    /* ROUTES PUBLIQUES */
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

    /* ROUTES PROTÉGÉES */
    @Bean
    @Order(2)
    public SecurityWebFilterChain securedSecurityChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                .pathMatchers("/images/**").hasRole("USER")
                .pathMatchers("/history/**").hasRole("USER")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(this::jwtAuthenticationConverter)
                )
            );

        return http.build();
    }

    /* JWT → REALM ROLES */
    private Mono<AbstractAuthenticationToken> jwtAuthenticationConverter(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (realmAccess != null && realmAccess.get("roles") instanceof List<?>) {
            authorities = ((List<?>) realmAccess.get("roles"))
                .stream()
                .map(role -> "ROLE_" + role.toString().toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        }

        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }
}
