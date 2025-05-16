package web.app.webflux_moldunity.filter;

import java.util.List;
import java.util.Map;

public interface AdFilter {
    FilterQuery createQuery(Map<String, List<String>> filters);
}
