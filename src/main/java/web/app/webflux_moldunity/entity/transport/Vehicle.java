package web.app.webflux_moldunity.entity.transport;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Vehicle {
    @NotEmpty private String brand;
    @NotEmpty private String model;
    @NotNull  private Integer year;
    @NotNull  private Integer mileage;
    @NotEmpty private String fuel;
    @NotEmpty private String gearBox;
    @NotNull  private Integer engineCapacity;
    @NotNull  private Integer power;
    @NotEmpty private String color;
    @NotEmpty private String steeringWheel;
}






