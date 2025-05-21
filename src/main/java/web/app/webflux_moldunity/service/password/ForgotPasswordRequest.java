package web.app.webflux_moldunity.service.password;


import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank String email,
        @NotBlank String password
){}
