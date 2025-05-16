package web.app.webflux_moldunity.filter.ad;


import web.app.webflux_moldunity.filter.FilterContext;
import web.app.webflux_moldunity.filter.FilterHandler;

import java.util.List;

public class BaseAdFilter {
    private static final String TABLE = "ads";
    private static final List<String> multipleFields = List.of("country");
    private static final List<String> rangeFields = List.of("price");

    public static void createFilter(FilterContext ctx) {
        FilterHandler.multipleFilter(multipleFields, TABLE, ctx);
        FilterHandler.rangeFilter(rangeFields, TABLE, ctx);
    }
}
