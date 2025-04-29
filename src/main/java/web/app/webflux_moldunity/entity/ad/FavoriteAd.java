package web.app.webflux_moldunity.entity.ad;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(value = "favorite_ads")
public class FavoriteAd {
    @Id
    private Long id;

    @NotEmpty
    private String username;

    @NotNull
    private Long userId;

    private LocalDateTime createdAt;

    @NotNull
    private Long adId;
}





