package web.app.webflux_moldunity.enums;

import web.app.webflux_moldunity.entity.Category;
import web.app.webflux_moldunity.entity.Subcategory;
import web.app.webflux_moldunity.entity.real_estate.House;
import web.app.webflux_moldunity.entity.transport.Car;
import web.app.webflux_moldunity.entity.transport.Transport;
import web.app.webflux_moldunity.entity.real_estate.RealEstate;

import java.util.Arrays;
import java.util.Optional;

public enum AdSubcategory {
    //************ Transport *******************
    CAR(100, "CAR", Transport.class, Car.class),

    //************ Real Estate *******************
    HOUSE(200, "HOUSE", RealEstate.class, House.class);

    //************ Home Appliances *******************
//    HOME_APPLIANCES(300, "HomeAppliances", Object.class, subcategoryClassType),

    //************ Furniture *******************
//    FURNITURE(400, "Furniture", Object.class, subcategoryClassType),

    //************ Electronics *******************
//    ELECTRONICS(500, "Electronics", Object.class, subcategoryClassType),
//    SERVICE(600, "Service", Object.class, subcategoryClassType),
//    JOB(700, "Job", Object.class, subcategoryClassType),
//    OTHER(800, "Other", Object.class, subcategoryClassType);

    private final Integer code;
    private final String label;
    private final Class<? extends Category> categoryClassType;
    private final Class<? extends Subcategory> subcategoryClassType;

    AdSubcategory(Integer code, String label, Class<? extends Category> classType, Class<? extends Subcategory> subcategoryClassType) {
        this.code = code;
        this.label = label;
        this.categoryClassType = classType;
        this.subcategoryClassType = subcategoryClassType;
    }

    public String getCategoryName() {
        return this.label;
    }

    public Integer getCode() {
        return this.code;
    }

    public Class<? extends Category> getCategoryType() {
        return categoryClassType;
    }

    public Class<? extends Subcategory> getSubcategoryType(){
        return subcategoryClassType;
    }

    public static Optional<AdSubcategory> fromLabel(String label) {
        return Arrays.stream(values())
                .filter(c -> c.label.equalsIgnoreCase(label))
                .findFirst();
    }

    public static Optional<AdSubcategory> fromSubcategory(Class<? extends Subcategory> subcategoryClass) {
        return Arrays.stream(values())
                .filter(c -> c.subcategoryClassType.equals(subcategoryClass))
                .findFirst();
    }

    public static boolean hasLabel(String label) {
        return Arrays.stream(values())
                .anyMatch(c -> c.label.equalsIgnoreCase(label));
    }
}
