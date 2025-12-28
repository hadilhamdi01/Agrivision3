package agrivision.example.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
public class SecurityConfig {

    // 🔓 CHAÎNE PUBLIQUE (REGISTER / LOGIN)
    @Bean
    @Order(1)
    public SecurityWebFilterChain publicSecurityChain(ServerHttpSecurity http) {

        http
            .securityMatcher(
                ServerWebExchangeMatchers.pathMatchers(
                    "/auth/**",
                    "/actuator/**"
                )
            )
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex.anyExchange().permitAll());

        return http.build();
    }

    // 🔐 CHAÎNE PROTÉGÉE (JWT)
    @Bean
    @Order(2)
    public SecurityWebFilterChain securedSecurityChain(ServerHttpSecurity http) {

        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex.anyExchange().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }
}
