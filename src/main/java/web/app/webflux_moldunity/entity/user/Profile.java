package web.app.webflux_moldunity.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import web.app.webflux_moldunity.entity.ad.Ad;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Profile(
    String username,
    LocalDateTime createdAt,
    List<Ad> ads
){}
