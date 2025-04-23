package web.app.webflux_moldunity.config.postgres;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
public class PostgresConfig extends AbstractR2dbcConfiguration {
    @Value("${postgresql.host}")
    private String host;

    @Value("${postgresql.port}")
    private int port;

    @Value("${postgresql.name}")
    private String database;

    @Value("${postgresql.username}")
    private String username;

    @Value("${postgresql.password}")
    private String password;

    @Override
    @NonNull
    @Bean
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(
                ConnectionFactoryOptions.builder()
                        .option(DRIVER, "postgresql")
                        .option(HOST, host)
                        .option(PORT, port)
                        .option(DATABASE, database)
                        .option(USER, username)
                        .option(PASSWORD, password)
                        .build()
        );
    }
}










