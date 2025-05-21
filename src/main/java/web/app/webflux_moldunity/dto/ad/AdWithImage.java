package web.app.webflux_moldunity.dto.ad;

import com.fasterxml.jackson.annotation.JsonInclude;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.entity.ad.AdImage;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AdWithImage(
        Ad ad,
        AdImage adImage
) {}
