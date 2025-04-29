package web.app.webflux_moldunity.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import web.app.webflux_moldunity.entity.ad.Ad;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Profile(
        UserProfile user,
        List<Ad> ads
){}
