package web.app.webflux_moldunity.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.user.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    @Transactional(readOnly = true)
    @Query(value = "select * from users where username = :username")
    Mono<User> findByUsername(@Param("username") String username);

    @Transactional(readOnly = true)
    @Query(value = "select username from users where username = :username")
    String existUsername(@Param("username") String username);
}
