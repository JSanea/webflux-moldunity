package web.app.webflux_moldunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.entity.ad.Subcategory;
import web.app.webflux_moldunity.exception.InvalidAdStructureException;

import java.util.List;

@Service
public class AdService {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DatabaseClient databaseClient;
    private final TransactionalOperator tx;

    @Autowired
    public AdService(R2dbcEntityTemplate r2dbcEntityTemplate, DatabaseClient databaseClient, TransactionalOperator transactionalOperator) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.databaseClient = databaseClient;
        this.tx = transactionalOperator;
    }

    public <S extends Subcategory> Mono<Ad> save(Ad ad, Class<S> subcategoryType){
        ad.setDateTimeFields();
        return r2dbcEntityTemplate.insert(Ad.class).using(ad)
                .flatMap(savedAd -> {
                    S subcategory = subcategoryType.cast(ad.getSubcategory());

                    if (null == subcategory)
                        return Mono.error(new InvalidAdStructureException("Ad category must not be null"));

                    subcategory.setAdId(savedAd.getId());

                    return r2dbcEntityTemplate.insert(subcategoryType).using(subcategory)
                            .flatMap(savedSubcategory -> {
                                savedAd.setSubcategory(savedSubcategory);
                                return Mono.just(savedAd);
                            });
                }).as(tx::transactional);
    }

    public <S extends Subcategory> Mono<Ad> getById(Long id, Class<S> subcategoryType) {
        return r2dbcEntityTemplate.selectOne(
                Query.query(Criteria.where("id").is(id)),
                Ad.class);
//        ).flatMap(ad -> r2dbcEntityTemplate.selectOne(
//                Query.query(Criteria.where("ad_id").is(ad.getId())),
//                categoryType
//        ).flatMap(subcategory -> {
//            ad.setSubcategory(category);
//            return Mono.just(ad);
//        }))).as(tx::transactional);
    }

    public Flux<Ad> getFluxAdsByUsername(String username){
        return databaseClient.sql("SELECT ads.* FROM ads WHERE ads.username = :username")
                .bind("username", username)
                .map((row, metadata) -> Ad.mapRowToAd(row)
                )
                .all();
    }

    public Mono<List<Ad>> getAdsByUsername(String username){
        return getFluxAdsByUsername(username).collectList();
    }

    public Mono<Long> getCountAdsByUsername(String username){
        return databaseClient.sql("SELECT count(*) AS cnt FROM ads WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> row.get("cnt", Long.class))
                .one();
    }

}
