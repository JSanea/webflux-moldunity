package web.app.webflux_moldunity.controller.password;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.service.UserService;
import web.app.webflux_moldunity.service.password.ForgotPasswordRequest;
import web.app.webflux_moldunity.service.password.ForgotPasswordService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordController {
    private final ForgotPasswordService forgotPasswordService;
    private final UserService userService;

    @PostMapping(value = "/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> sendVerificationCode(@Valid @RequestBody ForgotPasswordRequest request) {
        return forgotPasswordService.sendCode(request)
                .map(r -> r
                        ? ResponseEntity.ok("Verification code sent")
                        : ResponseEntity.internalServerError().body("Failed to send code")
                )
                .switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send code")));
    }

    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> resetPassword(@RequestBody Map<String, Integer> code){
        if (code == null || code.isEmpty()){
            return Mono.just(ResponseEntity.badRequest().body("Wrong request structure"));
        }

        var x = forgotPasswordService.getCredentials(code.get("code"));

        return Mono.justOrEmpty(x)
                .flatMap(u -> userService.resetPassword(u.email(), u.password()))
                .map(r -> r
                        ? ResponseEntity.ok("Ok")
                        : ResponseEntity.internalServerError().body("Error")
                )
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().body("Code is expired or wrong")))
                .onErrorResume(e -> {
                    log.error("Error to reset password: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.internalServerError().body("Error"));
                });
    }
}
