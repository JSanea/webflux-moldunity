package web.app.webflux_moldunity.enums;

import lombok.Data;
import lombok.Getter;
import web.app.webflux_moldunity.entity.transport.Car;

@Getter
public enum TransportSubcategory {
    CAR(1, "Car", Car.class);

    private final Integer code;
    private final String subcategory;
    private final Class<?> classType;

    TransportSubcategory(Integer code, String subcategory, Class<?> classType) {
        this.code = code;
        this.subcategory = subcategory;
        this.classType = classType;
    }
}
