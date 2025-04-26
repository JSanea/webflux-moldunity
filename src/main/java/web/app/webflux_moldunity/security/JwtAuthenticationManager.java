package web.app.webflux_moldunity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationManager(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

//        if (token == null || token.isBlank()) {
//            return Mono.error(new BadCredentialsException("Token is missing"));
//        }
//
//        return Mono.just(token)
//            .filter(jwtService::isTokenValid)
//            .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid token")))
//            .map(jwtService::extractUsername)
//            .flatMap(username -> {
//                List<GrantedAuthority> authorities = jwtService.extractRoles(token).stream()
//                        .map(SimpleGrantedAuthority::new)
//                        .toList();
//
//                return Mono.just(
//                        new UsernamePasswordAuthenticationToken(username, token, authorities)
//                );
//        });
        return null;
    }
}
