package web.app.webflux_moldunity.entity.ad;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.r2dbc.spi.Row;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.enums.AdType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Table(value = "ads")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Ad {
    @Id
    private Long id;
    @NotEmpty private String username;
    @NotEmpty private String offerType;
    @NotEmpty private String title;
    @NotEmpty private String categoryName;
    @NotEmpty private String subcategoryName;

    private String description;

    @Min(value = 1, message = "Price must be greater than 0")
    private Integer price;

    private Integer views = 0;

    private Subcategory subcategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime republishedAt;
    private Long userId;
    private List<AdImage> adImages;

    public void setSubcategoryNameFromAdType(AdType type){
        this.subcategoryName = type.getSubcategoryName();
    }

    public void setDateTimeFields(){
        var t = LocalDateTime.now();
        this.createdAt = t;
        this.updatedAt = t;
        this.republishedAt = t;
    }

    public static Ad mapRowToAd(Row row){
        return Ad.builder()
                .id(row.get("id", Long.class))
                .offerType(row.get("offer_type", String.class))
                .title(row.get("title", String.class))
                .description(row.get("description", String.class))
                .categoryName(row.get("category_name", String.class))
                .subcategoryName(row.get("subcategory_name", String.class))
                .price(row.get("price", Integer.class))
                .createdAt(row.get("created_at", LocalDateTime.class))
                .updatedAt(row.get("updated_at", LocalDateTime.class))
                .republishedAt(row.get("republished_at", LocalDateTime.class))
                .username(row.get("username", String.class))
                .userId(row.get("user_id", Long.class))
                .build();
    }
}















