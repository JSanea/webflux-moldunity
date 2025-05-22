package web.app.webflux_moldunity.entity.ad;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import web.app.webflux_moldunity.entity.real_estate.Apartment;
import web.app.webflux_moldunity.entity.real_estate.Home;
import web.app.webflux_moldunity.entity.transport.BusMinibus;
import web.app.webflux_moldunity.entity.transport.Car;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "_type"
)
@JsonSubTypes({
        // Transport
        @JsonSubTypes.Type(value = Car.class, name = "Car"),
        @JsonSubTypes.Type(value = BusMinibus.class, name = "BusMinibus"),

        // Real Estate
        @JsonSubTypes.Type(value = Apartment.class, name = "Apartment"),
        @JsonSubTypes.Type(value = Home.class, name = "Home")
})
public interface Subcategory {
    Long getId();

    void setAdId(Long id);
}
