package web.app.webflux_moldunity.entity.transport;


import jakarta.validation.constraints.NotBlank;
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
    @NotBlank private String body;
    @NotNull  private Long AdId;
}
