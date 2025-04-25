package web.app.webflux_moldunity.entity.real_estate;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.entity.Subcategory;

@Data
@Table(value = "houses")
public class House implements Subcategory {
    @Id
    private Long id;

    private Long categoryId;
}
