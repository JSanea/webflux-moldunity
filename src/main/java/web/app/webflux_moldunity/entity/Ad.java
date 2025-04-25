package web.app.webflux_moldunity.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import web.app.webflux_moldunity.enums.AdSubcategory;

import java.time.LocalDateTime;

@Data
@Table(value = "ads")
public class Ad {
    @Id
    private Long id;
    private String username;
    private String offerType;
    private String title;
    private String categoryName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private LocalDateTime republishedAt;
    private Category category;
    private Long userId;

    public void setCategoryName(AdSubcategory categoryEnum) {
        this.categoryName = categoryEnum.getCategoryName();
    }
}
