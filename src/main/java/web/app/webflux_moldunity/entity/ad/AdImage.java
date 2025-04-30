package web.app.webflux_moldunity.entity.ad;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(value = "ad_images")
public class AdImage {
    @Id
    private Long id;

    @NotEmpty
    private String url;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private Long adId;
}






