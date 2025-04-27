package web.app.webflux_moldunity.entity.real_estate;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.entity.ad.Category;
import web.app.webflux_moldunity.entity.ad.Subcategory;

@Getter
@Setter
@Table(value = "real_estates")
public class RealEstate implements Category {
    @Id
    private Long id;
    private Subcategory subcategory;
    private Long adId;
}
