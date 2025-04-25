package web.app.webflux_moldunity.entity.real_estate;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.entity.Category;
import web.app.webflux_moldunity.entity.Subcategory;

@Data
@Table(value = "real_estates")
public class RealEstate implements Category {
    @Id
    private Long id;
    private Subcategory subcategory;
    private Long adId;
}
