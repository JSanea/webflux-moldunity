package web.app.webflux_moldunity.entity.ad;

public interface Category {
    Long getId();

    void setAdId(Long id);

    <T extends Subcategory> void setSubcategory(T t);

    <T extends Subcategory> T getSubcategory();
}
