package web.app.webflux_moldunity.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "users")
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;
    private String country;
    private String location;
    private String phone;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
