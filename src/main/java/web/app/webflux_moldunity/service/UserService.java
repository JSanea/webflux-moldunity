package web.app.webflux_moldunity.service;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.user.User;
import web.app.webflux_moldunity.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DatabaseClient databaseClient;
    private final TransactionalOperator tx;
    private final PasswordEncoder passwordEncoder;


    public Mono<User> getUserByName(String username){
        return databaseClient.sql("SELECT * FROM users WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> User.mapRowToUser(row)).one();
    }

    public Mono<User> findByUsernameOrEmail(String username, String email){
        return databaseClient.sql("SELECT * FROM users WHERE username = :username OR email = :email")
                .bind("username", username)
                .bind("email", email)
                .map((row, metadata) -> User.mapRowToUser(row))
                .one();
    }

    public Mono<Long> getIdByUsername(String username){
        return databaseClient.sql("SELECT users.id AS id FROM users WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> row.get("id", Long.class))
                .one();
    }

    public Mono<User> save(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return r2dbcEntityTemplate.insert(user).as(tx::transactional);
    }
}
