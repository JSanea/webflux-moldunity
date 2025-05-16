package web.app.webflux_moldunity.filter;

import org.springframework.stereotype.Component;
import web.app.webflux_moldunity.filter.transport.CarFilter;

import java.util.Map;

@Component
public class FilterMap {
    private final Map<String, AdFilter> filterMap = Map.of(
            "Car", new CarFilter()
    );

    public AdFilter getFilter(String key){
        return filterMap.get(key);
    }
}
