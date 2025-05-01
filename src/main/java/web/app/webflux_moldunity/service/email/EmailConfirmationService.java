package web.app.webflux_moldunity.service.email;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import web.app.webflux_moldunity.entity.user.User;
import web.app.webflux_moldunity.util.Expiry;
import web.app.webflux_moldunity.util.ExpiryMap;

@Service
@Slf4j
public class EmailConfirmationService {
    @Value("${email.username}")
    private String FROM;
    private final EmailService emailService;
    private final ExpiryMap<String, User> expiryUsers = new ExpiryMap<>();

    public EmailConfirmationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public Mono<Boolean> sendEmail(@NotNull User user) {
        String key = UUID.randomUUID().toString();

        return Mono.fromRunnable(() -> {
                    // Remove any previous entries for this email
                    for (String k : expiryUsers.getKeySet()) {
                        var v = expiryUsers.get(k);
                        if (v.isPresent() && user.getEmail().equals(v.get().getEmail())) {
                            expiryUsers.remove(k);
                            break;
                        }
                    }
                    expiryUsers.put(key, new Expiry<>(user, 5L));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then(emailService.sendEmail(
                        user.getEmail(),
                        FROM,
                        "Moldunity.md | Email Confirmation",
                        "Pentru a confirma email-ul accesati link-ul:\nhttp://localhost:8080/register?key=" + key
                ))
                .doOnSuccess(v -> log.info("Email successfully sent to {}", user.getEmail()))
                .thenReturn(true)
                .onErrorResume(e -> {
                    log.error("Email send failed to {}: {}", user.getEmail(), e.getMessage());
                    return Mono.just(false);
                });
    }

    public Optional<User> getUser(String key){
        var u = expiryUsers.get(key);
        expiryUsers.remove(key);
        return u;
    }
}












