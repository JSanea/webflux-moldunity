package web.app.webflux_moldunity.entity.transport;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.entity.ad.Subcategory;

@Getter
@Setter
@Table(value = "cars")
public class Car extends Vehicle implements Subcategory {
    @Id
    private Long id;

    @NotEmpty
    private String body;

    @NotNull
    private Long AdId;
}
