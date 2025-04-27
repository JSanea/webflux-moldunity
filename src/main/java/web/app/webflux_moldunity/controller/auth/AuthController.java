package web.app.webflux_moldunity.controller.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.security.AuthRequest;
import web.app.webflux_moldunity.security.AuthResponse;

@RestController
public class AuthController {

    @PostMapping(value = "/login")
    public Mono<AuthResponse> login(@RequestBody AuthRequest authRequest){
        return null;
    }

    @PostMapping(value = "/logout")
    public Mono<String> logout(){
        return null;
    }

    @PostMapping(value = "/refresh")
    public Mono<String> refreshToken(){
        return null;
    }
}
