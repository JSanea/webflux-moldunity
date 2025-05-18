package web.app.webflux_moldunity.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.dto.Profile;
import web.app.webflux_moldunity.service.UserService;

@RestController
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/profile/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Profile>> getProfile(@PathVariable String username){
        return userService.getProfileByName(username)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(e -> {
                    log.error("Error to fetch profile by name: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
