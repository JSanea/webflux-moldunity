package web.app.webflux_moldunity.dto.ad;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.entity.ad.Subcategory;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AdDetails(
   @Valid Ad ad,
   @Valid Subcategory subcategory
){}
