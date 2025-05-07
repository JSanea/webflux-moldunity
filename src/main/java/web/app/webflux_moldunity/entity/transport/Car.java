package web.app.webflux_moldunity.entity.transport;


import io.r2dbc.spi.Row;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.entity.ad.Subcategory;

@Getter
@Setter
@Builder
@Table(name = "cars")
public class Car extends Vehicle implements Subcategory {
    @Id
    private Long id;
    @NotEmpty private String body;
    @NotNull  private Long AdId;


    public Car fromRow(Row row) {
        var c = Car.builder().build();
        c.setId(row.get("id", Long.class));
        c.setBrand(row.get("brand", String.class));
        return c;
    }
}
