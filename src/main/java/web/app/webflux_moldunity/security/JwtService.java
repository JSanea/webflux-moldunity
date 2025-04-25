package web.app.webflux_moldunity.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class JwtService {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-expiration-minutes}")
    private long tokenExpiration;
}
