package web.app.webflux_moldunity.security;

public final class WhiteUrl {
    private WhiteUrl(){}

    public static String[] urls(){
        return new String[]{
                "/login",
                "/register",
                "/ads/**",
                "/profile/**",
                "/auth/refresh",
                "/is-authenticate",
                "/v3/api-docs",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/docs"
        };
    }
}
