package web.app.webflux_moldunity.filter.transport;

import web.app.webflux_moldunity.filter.FilterContext;
import web.app.webflux_moldunity.filter.FilterHandler;

import java.util.List;


public class VehicleFilter {
    private static final List<String> singleFields = List.of("brand");
    private static final List<String> multipleFields = List.of("model", "fuel", "gear_box", "color", "steering_wheel");
    private static final List<String> rangeFields = List.of("year", "mileage", "engine_capacity", "power");

    public static void createFilter(String table, FilterContext ctx) {
        FilterHandler.applyFilters(table, ctx, singleFields, multipleFields, rangeFields);
    }
}