package web.app.webflux_moldunity.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import web.app.webflux_moldunity.entity.Ad;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UserAds(
        User user,
        List<Ad> ads
){}
