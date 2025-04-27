package web.app.webflux_moldunity.security;

import java.util.List;


public class WhiteUrl {
    public static String[] urls(){
        return new String[]{
                "/login",
                "/ads/**",
                "/v3/api-docs"
        };
    }
}
