package web.app.webflux_moldunity.entity.transport;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.entity.Subcategory;

@Data
@Table(value = "cars")
public class Car implements Subcategory {
    @Id
    private Long id;

    private Long categoryId;
}
