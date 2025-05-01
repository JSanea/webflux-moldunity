package web.app.webflux_moldunity.entity.user;


import java.time.LocalDateTime;

public record UserProfile(
        String username,
        String country,
        String location,
        LocalDateTime createdAt
){}
