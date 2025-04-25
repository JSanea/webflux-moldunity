package web.app.webflux_moldunity.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.user.User;
import web.app.webflux_moldunity.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DatabaseClient databaseClient;

    @Autowired
    public UserService(UserRepository userRepository, R2dbcEntityTemplate r2dbcEntityTemplate, DatabaseClient databaseClient) {
        this.userRepository = userRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.databaseClient = databaseClient;
    }

    public Mono<User> getUserCredentialsByName(String username){
        return databaseClient.sql("SELECT * FROM users WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> new User(
                        row.get("username", String.class),
                        row.get("password", String.class),
                        row.get("role",     String.class)
                ))
                .one();
    }
}
