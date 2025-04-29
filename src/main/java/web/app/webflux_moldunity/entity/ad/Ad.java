package web.app.webflux_moldunity.entity.ad;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.enums.AdType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Table(value = "ads")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Ad {
    @Id
    private Long id;

    @NotEmpty
    private String username;

    @NotEmpty
    private String offerType;

    @NotEmpty
    private String title;

    @NotEmpty
    private String categoryName;

    @NotEmpty
    private String subcategoryName;

    @NotEmpty
    private String description;

    @Min(value = 1, message = "Price must be greater than 0")
    private Integer price;

    private Integer views = 0;

    @NotNull
    private Subcategory subcategory;

    private List<AdImage> adImages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime republishedAt;

    @NotNull
    private Long userId;

    public void setSubcategoryNameFromAdType(AdType type){
        this.subcategoryName = type.getSubcategoryName();
    }

    public void setDateTimeFields(){
        var t = LocalDateTime.now();
        this.createdAt = t;
        this.updatedAt = t;
        this.republishedAt = t;
    }
}















