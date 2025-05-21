package web.app.webflux_moldunity.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.dto.Profile;
import web.app.webflux_moldunity.dto.UserProfile;
import web.app.webflux_moldunity.entity.user.User;
import web.app.webflux_moldunity.enums.ChangePasswordStatus;
import web.app.webflux_moldunity.exception.UserServiceException;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DatabaseClient databaseClient;
    private final TransactionalOperator tx;
    private final PasswordEncoder passwordEncoder;
    private final AdService adService;

    public Mono<User> findUserByName(String username) {
        return databaseClient.sql("SELECT * FROM users WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> User.mapRowToUser(row))
                .one()
                .onErrorResume(e -> {
                    log.error("Error fetching user by name: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to fetch user by name"));
                });
    }

    public Mono<User> findByUsernameOrEmail(String username, String email) {
        return databaseClient.sql("SELECT * FROM users WHERE username = :username OR email = :email")
                .bind("username", username)
                .bind("email", email)
                .map((row, metadata) -> User.mapRowToUser(row))
                .one()
                .onErrorResume(e -> {
                    log.error("Error fetching user by name or email: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to fetch user by name or email"));
                });
    }

    public Mono<Long> findIdByUsername(String username) {
        return databaseClient.sql("SELECT users.id AS id FROM users WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> row.get("id", Long.class))
                .one()
                .onErrorResume(e -> {
                    log.error("Error fetching user id by name: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to fetch user id by name"));
                });
    }

    public Mono<User> save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return r2dbcEntityTemplate
                .insert(user)
                .as(tx::transactional)
                .onErrorResume(e -> {
                    log.error("Error inserting User: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to insert User"));
                });
    }

    public Mono<Profile> getProfileByName(String name){
        return r2dbcEntityTemplate.selectOne(
                Query.query(Criteria.where("username").is(name)),
                User.class
        )
        .map(x -> new UserProfile(
                    x.getUsername(),
                    x.getCountry(),
                    x.getLocation(),
                    x.getCreatedAt()
            )
        )
        .flatMap(user -> adService.getByUsername(name).map(ads -> new Profile(user, ads)))
        .onErrorResume(e -> {
            log.error("Error to fetch profile by name: {}", e.getMessage(), e);
            return Mono.error(new UserServiceException("Error to fetch profile by name"));
        });
    }

    public Mono<ChangePasswordStatus> changePassword(String username, String currentPass, String newPass){
        return findUserByName(username)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    if(!passwordEncoder.matches(currentPass, user.getPassword())){
                        return Mono.just(ChangePasswordStatus.INVALID_CURRENT_PASSWORD);
                    }
                    user.setPassword(passwordEncoder.encode(newPass));
                    user.setUpdatedAt(LocalDateTime.now());
                    return r2dbcEntityTemplate.update(user).thenReturn(ChangePasswordStatus.SUCCESS);
                })
                .onErrorResume(e -> {
                    log.error("Error to change password: {}", e.getMessage(), e);
                    return Mono.just(ChangePasswordStatus.ERROR);
                });
    }

    public Mono<Boolean> resetPassword(String email, String password){
        return r2dbcEntityTemplate.selectOne(
                Query.query(Criteria.where(email).is(email)),
                User.class
        )
        .switchIfEmpty(Mono.error(new RuntimeException("User not found by email: " + email)))
        .flatMap(user -> {
            user.setPassword(passwordEncoder.encode(password));
            user.setUpdatedAt(LocalDateTime.now());
            return r2dbcEntityTemplate.update(user).thenReturn(true);
        })
        .onErrorResume(e -> {
            log.error("Error to reset password: {}", e.getMessage(), e);
            return Mono.error(new RuntimeException("Error to reset password"));
        });
    }

    public Mono<Boolean> existsEmail(String email){
        return r2dbcEntityTemplate.exists(
                Query.query(Criteria.where("email").is(email)),
                User.class
        )
        .onErrorResume(e -> {
            log.error("Error to check exists email: {}", e.getMessage(), e);
            return Mono.error(new UserServiceException("Error to check exists email"));
        });
    }
}

















