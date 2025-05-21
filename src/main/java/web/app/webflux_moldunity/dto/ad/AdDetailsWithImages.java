package web.app.webflux_moldunity.dto.ad;

import com.fasterxml.jackson.annotation.JsonInclude;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.entity.ad.AdImage;
import web.app.webflux_moldunity.entity.ad.Subcategory;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AdDetailsWithImages(
        Ad ad,
        Subcategory subcategory,
        List<AdImage> adImages
) {}
