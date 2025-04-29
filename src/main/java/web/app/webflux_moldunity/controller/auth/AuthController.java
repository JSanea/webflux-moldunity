package web.app.webflux_moldunity.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.security.AuthRequest;
import web.app.webflux_moldunity.security.AuthResponse;
import web.app.webflux_moldunity.security.JwtTokenProvider;

@RestController
@Slf4j
public class AuthController {
    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(ReactiveUserDetailsService reactiveUserDetailsService, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.reactiveUserDetailsService = reactiveUserDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/login")
    @Operation(summary = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest authRequest){
        return reactiveUserDetailsService.findByUsername(authRequest.username())
                .filter(u -> passwordEncoder.matches(authRequest.password(), u.getPassword()))
                .map(jwtTokenProvider::generateToken)
                .map(AuthResponse::new)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(""))))
                .onErrorResume(ex -> {
                    log.error("Login error: {}", ex.getMessage(), ex);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new AuthResponse("")));
                });
    }

    @PostMapping(value = "/logout")
    public Mono<String> logout(){
        return null;
    }

    @PostMapping(value = "/refresh")
    public Mono<AuthResponse> refreshToken(){
        return null;
    }
}
