package web.app.webflux_moldunity.service.password;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public record ChangePasswordRequest(
        @NotBlank String current,
        @NotBlank String fresh
){}
