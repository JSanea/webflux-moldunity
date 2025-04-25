package web.app.webflux_moldunity.enums;

import web.app.webflux_moldunity.entity.Category;
import web.app.webflux_moldunity.entity.Subcategory;
import web.app.webflux_moldunity.entity.real_estate.Apartment;
import web.app.webflux_moldunity.entity.real_estate.House;
import web.app.webflux_moldunity.entity.transport.Car;
import web.app.webflux_moldunity.entity.transport.Transport;
import web.app.webflux_moldunity.entity.real_estate.RealEstate;

import java.util.Arrays;
import java.util.Optional;

public enum AdType {
    //************ Transport *******************
    CAR(100, "Transport", "Car", Transport.class, Car.class),

    //************ Real Estate *******************
    APARTMENT(200, "Real-Estate","Apartment", RealEstate.class, Apartment.class),
    HOUSE(    201, "Real-Estate","House",     RealEstate.class, House.class);

    //************ Home Appliances *******************
//    HOME_APPLIANCES(300, "Home-Appliances", Object.class, subcategoryClassType),

    //************ Furniture *******************
//    FURNITURE(400, "Furniture", Object.class, subcategoryClassType),

    //************ Electronics *******************
//    ELECTRONICS(500, "Electronics", Object.class, subcategoryClassType),
//    SERVICE(600, "Service", Object.class, subcategoryClassType),
//    JOB(700, "Job", Object.class, subcategoryClassType),
//    OTHER(800, "Other", Object.class, subcategoryClassType);

    private final Integer code;
    private final String categoryName;
    private final String subcategoryName;
    private final Class<? extends Category> categoryClassType;
    private final Class<? extends Subcategory> subcategoryClassType;

    AdType(Integer code, String categoryName, String subcategoryName, Class<? extends Category> classType, Class<? extends Subcategory> subcategoryClassType) {
        this.code = code;
        this.categoryName = categoryName;
        this.subcategoryName = subcategoryName;
        this.categoryClassType = classType;
        this.subcategoryClassType = subcategoryClassType;
    }

    public String getSubcategoryName() {
        return this.subcategoryName;
    }

    public String getCategoryName(){
        return this.categoryName;
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

    public static Optional<AdType> fromSubcategoryName(String name) {
        return Arrays.stream(values())
                .filter(c -> c.subcategoryName.equalsIgnoreCase(name))
                .findFirst();
    }

    public static Optional<AdType> fromSubcategory(Class<? extends Subcategory> subcategoryClass) {
        return Arrays.stream(values())
                .filter(c -> c.subcategoryClassType.equals(subcategoryClass))
                .findFirst();
    }

    public static boolean hasSubcategoryName(String name) {
        return Arrays.stream(values())
                .anyMatch(c -> c.subcategoryName.equalsIgnoreCase(name));
    }
}
