package web.app.webflux_moldunity.filter;

import java.util.Map;

public record FilterQuery(String sql, String countSql, Map<String, Object> params) {
}
