package web.app.webflux_moldunity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {
    private static final String BEARER = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtServerAuthenticationConverter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(BEARER))
                .map(authHeader -> authHeader.substring(BEARER.length()))
                .map(token -> new JwtAuthenticationToken(token, createUserDetails(token)));
    }

    private UserDetails createUserDetails(String token){
        String username = jwtTokenProvider.extractUsername(token);
        String role = jwtTokenProvider.extractRole(token);
        return User.withUsername(username)
                .password("")
                .roles(role)
                .build();
    }
}

















