package web.app.webflux_moldunity.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.enums.AdType;

import java.time.LocalDateTime;

@Data
@Table(value = "ads")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Ad {
    @Id
    private Long id;
    private String username;
    private String offerType;
    private String title;
    private String categoryName;
    private String subcategoryName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private LocalDateTime republishedAt;
    private Category category;
    private Long userId;

    public void setCategoryName(AdType type) {
        this.categoryName = type.getCategoryName();
    }

    public void setSubcategoryName(AdType type){
        this.subcategoryName = type.getSubcategoryName();
    }
}
