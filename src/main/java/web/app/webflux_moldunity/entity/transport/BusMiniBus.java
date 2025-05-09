package web.app.webflux_moldunity.entity.transport;

import io.r2dbc.spi.Row;
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
@Table(name = "buses_minibuses")
public class BusMiniBus extends Vehicle implements Subcategory{
    @Id
    private Long id;
    @NotNull private Integer numSeats;
    @NotNull private Long AdId;


    public BusMiniBus fromRow(Row row) {
        return null;
    }
}
