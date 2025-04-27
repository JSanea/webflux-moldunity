package web.app.webflux_moldunity.entity.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "users")
public class User {
    @Id
    private Long id;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Invalid email")
    @NotEmpty
    private String email;
    @NotEmpty
    private String role;

    @NotEmpty
    private String country;

    @NotEmpty
    private String location;

    @NotEmpty
    private String phone;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}





















