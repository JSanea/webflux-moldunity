package web.app.webflux_moldunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.entity.ad.Category;
import web.app.webflux_moldunity.entity.ad.Subcategory;
import web.app.webflux_moldunity.exception.InvalidAdStructureException;

@Service
public class AdService {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TransactionalOperator tx;

    @Autowired
    public AdService(R2dbcEntityTemplate r2dbcEntityTemplate, TransactionalOperator transactionalOperator) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.tx = transactionalOperator;
    }

    public <C extends Category, S extends Subcategory> Mono<Ad> save(Ad ad, Class<C> categoryType, Class<S> subcategoryType){
        return r2dbcEntityTemplate.insert(Ad.class).using(ad)
                .flatMap(savedAd -> {
                    C category = categoryType.cast(ad.getCategory());

                    if (null == category)
                        return Mono.error(new InvalidAdStructureException("Ad category must not be null"));

                    category.setAdId(savedAd.getId());

                    return r2dbcEntityTemplate.insert(categoryType).using(category)
                            .flatMap(savedCategory -> {
                                S subcategory = subcategoryType.cast(savedCategory.getSubcategory());

                                if (null == subcategory)
                                    return Mono.error(new InvalidAdStructureException("Ad subcategory must not be null"));

                                subcategory.setCategoryId(savedCategory.getId());

                                return r2dbcEntityTemplate.insert(subcategoryType).using(subcategory)
                                        .flatMap(savedSubcategory -> {
                                            savedCategory.setSubcategory(savedSubcategory);
                                            savedAd.setCategory(savedCategory);
                                            return Mono.just(savedAd);
                                        });
                            });
                }).as(tx::transactional);
    }

    public <C extends Category, S extends Subcategory> Mono<Ad> getById(Long id, Class<C> categoryType, Class<S> subcategoryType) {
        return r2dbcEntityTemplate.selectOne(
                Query.query(Criteria.where("id").is(id)),
                Ad.class);
//        ).flatMap(ad -> r2dbcEntityTemplate.selectOne(
//                Query.query(Criteria.where("ad_id").is(ad.getId())),
//                categoryType
//        ).flatMap(category -> r2dbcEntityTemplate.selectOne(
//                Query.query(Criteria.where("category_id").is(category.getId())),
//                subcategoryType
//        ).flatMap(subcategory -> {
//            category.setSubcategory(subcategory);
//            ad.setCategory(category);
//            return Mono.just(ad);
//        }))).as(tx::transactional);
    }

}
