package web.app.webflux_moldunity.security;

public final class WhiteUrl {

    private WhiteUrl(){}

    public static String[] urls(){
        return new String[]{
                "/login",
                "/ads/**",
                "/auth/refresh",
                "/v3/api-docs",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/docs"
        };
    }
}
