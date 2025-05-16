package web.app.webflux_moldunity.filter;


import web.app.webflux_moldunity.filter.ad.BaseAdFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseFilter {
    protected FilterQuery buildFilterQuery(String table,
                                           Map<String, List<String>> filters,
                                           Consumer<FilterContext> extraFilterLogic) {
        if (filters == null || filters.isEmpty()) {
            return new FilterQuery("WHERE 1=1 ORDER BY ads.republished_at DESC", "WHERE 1=1", Collections.emptyMap());
        }

        String joinSql = "INNER JOIN " + table + " ON ads.id = " + table + ".ad_id ";
        String countSql = "SELECT count(ads.id) FROM ads ";
        StringBuilder whereSql = new StringBuilder("WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        FilterContext ctx = new FilterContext(filters, whereSql, params);

        BaseAdFilter.createFilter(ctx);

        String sort = Sort.sortBy(filters.get("sort"));

        return new FilterQuery(
                joinSql + whereSql + sort,
                countSql + joinSql + whereSql,
                params
        );

    }
}
