package web.app.webflux_moldunity.entity.ad;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AdJoinImage(
        Ad ad,
        AdImage adImage
) {
}
