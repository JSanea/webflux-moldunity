package web.app.webflux_moldunity.entity.transport;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.entity.ad.Subcategory;

@Getter
@Setter
@Table(value = "buses_minibuses")
public class BusMiniBus implements Subcategory {
    @Id
    private Long id;

    private Long AdId;
}
