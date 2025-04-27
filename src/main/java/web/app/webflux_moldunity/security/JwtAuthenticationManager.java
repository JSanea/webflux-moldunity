package web.app.webflux_moldunity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.exception.JwtAuthenticationException;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthenticationManager(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        if(token == null)
            return Mono.error(new JwtAuthenticationException("Token is missing"));

        String username = jwtTokenProvider.extractUsername(token);

        return userDetailsService.findByUsername(username)
                .filter(userDetails -> jwtTokenProvider.isTokenValid(token, userDetails))
                .map(userDetails -> (Authentication)new JwtAuthenticationToken(token, userDetails))
                .switchIfEmpty(Mono.error(new JwtAuthenticationException("Invalid token or token expired")));
    }
}
