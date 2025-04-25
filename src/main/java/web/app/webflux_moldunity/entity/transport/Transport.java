package web.app.webflux_moldunity.entity.transport;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.entity.Category;
import web.app.webflux_moldunity.entity.Subcategory;


@Data
@Table(value = "transports")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Transport implements Category {
    @Id
    private Long id;
    private Subcategory subcategory;
    private Long adId;
}
