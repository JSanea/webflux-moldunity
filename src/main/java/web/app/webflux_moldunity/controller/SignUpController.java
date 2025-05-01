package web.app.webflux_moldunity.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.user.User;
import web.app.webflux_moldunity.service.UserService;
import web.app.webflux_moldunity.service.email.EmailConfirmationService;


@RestController
@AllArgsConstructor
@Slf4j
public class SignUpController {
    private final EmailConfirmationService emailConfirmationService;
    private final UserService userService;

    @PostMapping(value = "/register",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> confirmUserEmail(@RequestBody User user){
        return userService.findByUsernameOrEmail(user.getUsername(), user.getEmail())
                .map(u -> {
                    if(user.getUsername().equals(u.getUsername()))
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Username already exists");

                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Email already exists");
                })
                .switchIfEmpty(emailConfirmationService.sendEmail(user)
                                .map(success -> success
                                ? ResponseEntity.ok("Email sent successfully")
                                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Failed to send email. Please try again later"))
                ).onErrorResume(e -> {
                    log.error("Send email confirmation error: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @GetMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<User>> register(@RequestParam String key){
        return Mono.justOrEmpty(emailConfirmationService.getUser(key))
                .flatMap(user -> {
                    user.setDateTimeFields();
                    return userService.save(user)
                            .map(ResponseEntity::ok);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> {
                    log.error("Registration error: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}
