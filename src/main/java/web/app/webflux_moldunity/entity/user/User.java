package web.app.webflux_moldunity.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.r2dbc.spi.Row;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(value = "users")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User {
    @Id
    private Long id;
    @NotEmpty private String username;
    @NotEmpty private String password;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Invalid email")
    @NotEmpty private String email;
    @NotEmpty private String role;
    @NotEmpty private String country;
    @NotEmpty private String location;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void setDateTimeFields(){
        var t = LocalDateTime.now();
        this.createdAt = t;
        this.updatedAt = t;
    }

    public static User mapRowToUser(Row row){
        return User.builder()
                //.id(row.get("id", Long.class))
                .username(row.get("username", String.class))
                .password(row.get("password", String.class))
                .email(row.get("email", String.class))
                .role(row.get("role", String.class))
                .country(row.get("country", String.class))
                .location(row.get("location", String.class))
                .phone(row.get("phone", String.class))
                .createdAt(row.get("created_at", LocalDateTime.class))
                .updatedAt(row.get("updated_at", LocalDateTime.class))
                .build();
    }
}





















