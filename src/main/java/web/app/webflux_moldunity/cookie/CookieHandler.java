package web.app.webflux_moldunity.cookie;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CookieHandler {
    private final CookieProperties authCookieProperties;

    public ResponseCookie createCookie(String name, String value, Long maxAge){
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(authCookieProperties.isSecure()) // true in production
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                //.domain("domain.com")
                .build();
    }
}







