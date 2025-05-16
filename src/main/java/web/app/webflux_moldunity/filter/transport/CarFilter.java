package web.app.webflux_moldunity.filter.transport;


import web.app.webflux_moldunity.filter.*;
import web.app.webflux_moldunity.filter.ad.BaseAdFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CarFilter implements AdFilter {
    private static final String TABLE = "cars";
    private static final List<String> multipleFields = List.of("body");

    @Override
    public FilterQuery createQuery(Map<String, List<String>> filters) {
        if (filters == null || filters.isEmpty()) {
            return new FilterQuery("WHERE 1=1 ORDER BY ads.republished_at DESC", "WHERE 1=1", Collections.emptyMap());
        }

        String joinSql = "INNER JOIN " + TABLE + " ON ads.id = " + TABLE + ".ad_id ";
        String countSql = "SELECT count(ads.id) FROM ads ";
        StringBuilder whereSql = new StringBuilder("WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();
        FilterContext ctx = new FilterContext(filters, whereSql, params);
        String sort;

        // Ad
        BaseAdFilter.createFilter(ctx);

        // Vehicle
        VehicleFilter.createFilter(TABLE, ctx);

        // Car
        FilterHandler.multipleFilter(multipleFields, TABLE, ctx);

        sort = Sort.sortBy(filters.get("sort"));

        return new FilterQuery(
                joinSql + whereSql + sort,
                countSql + joinSql + whereSql,
                params
        );
    }
}
