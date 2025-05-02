package web.app.webflux_moldunity.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.entity.ad.AdImage;
import web.app.webflux_moldunity.entity.ad.AdJoinImage;
import web.app.webflux_moldunity.entity.ad.Subcategory;
import web.app.webflux_moldunity.exception.InvalidAdStructureException;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AdService {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DatabaseClient databaseClient;
    private final TransactionalOperator tx;

    public <S extends Subcategory> Mono<Ad> save(Ad ad, Class<S> subcategoryType) {
        ad.setDateTimeFields();
        return r2dbcEntityTemplate.insert(Ad.class).using(ad)
                .flatMap(savedAd -> {
                    S subcategory = subcategoryType.cast(ad.getSubcategory());

                    if (null == subcategory)
                        return Mono.error(new InvalidAdStructureException("Ad subcategory must not be null"));

                    subcategory.setAdId(savedAd.getId());

                    return r2dbcEntityTemplate.insert(subcategoryType).using(subcategory)
                            .flatMap(savedSubcategory -> {
                                savedAd.setSubcategory(savedSubcategory);
                                return Mono.just(savedAd);
                            });
                })
                .as(tx::transactional)
                .onErrorResume(e -> {
                    log.error("Error inserting Ad: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to insert Ad"));
                });
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

    public Flux<Ad> getFluxAdsByUsername(String username) {
        String sql = """
                SELECT ads.*,
                    ad_images.id AS ad_images_id,
                    ad_images.url AS ad_images_url,
                    ad_images.created_at AS ad_images_created_at,
                    ad_images.ad_id AS ad_images_ad_id
                FROM ads
                LEFT JOIN ad_images ON ad_images.ad_id = ads.id
                WHERE ads.username = :username""";

        return databaseClient.sql(sql)
                .bind("username", username)
                .map((row, metadata) -> {
                    Ad ad = Ad.mapRowToAd(row);
                    AdImage adImage = AdImage.mapRowToAdImage(row);
                    return new AdJoinImage(ad, adImage);
                })
                .all()
                .collectMultimap(a -> a.ad().getId())
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> {
                    List<AdJoinImage> adJoinImages = new ArrayList<>(entry.getValue());
                    Ad ad = adJoinImages.get(0).ad();

                    List<AdImage> images = entry.getValue().stream()
                            .map(AdJoinImage::adImage)
                            .filter(adImage -> adImage.getId() != null)
                            .toList();

                    ad.setAdImages(images);
                    return ad;
                })
                .onErrorResume(e -> {
                    log.error("Error fetching Ads by username: {}", e.getMessage(), e);
                    return Flux.error(new RuntimeException("Failed to fetch Ads by username"));
                });
    }

    public Mono<List<Ad>> getAdsByUsername(String username) {
        return getFluxAdsByUsername(username)
                .collectList()
                .onErrorResume(e -> {
                    log.error("Error fetching Ads list: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to fetch Ads list"));
                });
    }

    public Mono<Long> getCountAdsByUsername(String username) {
        return databaseClient.sql("SELECT count(*) AS cnt FROM ads WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> row.get("cnt", Long.class))
                .one()
                .onErrorResume(e -> {
                    log.error("Error counting Ads by username: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to count Ads by username"));
                });
    }

    public Mono<AdImage> saveImageUrl(AdImage image) {
        return r2dbcEntityTemplate
                .insert(image)
                .as(tx::transactional)
                .onErrorResume(e -> {
                    log.error("Error saving image url: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to save image url"));
                });
    }

    public <S extends Subcategory> Mono<Ad> update(Ad ad, Class<S> subcategory) {
        return r2dbcEntityTemplate.select(Ad.class)
                .matching(Query.query(Criteria.where("id").is(ad.getId())))
                .one()
                .switchIfEmpty(Mono.error(new RuntimeException("Ad not found")))
                .flatMap(existingAd ->
                        r2dbcEntityTemplate.update(ad)
                                .flatMap(updatedAd -> {
                                    S s = subcategory.cast(ad.getSubcategory());
                                    return r2dbcEntityTemplate.update(s).thenReturn(updatedAd);
                                })
                )
                .as(tx::transactional)
                .onErrorResume(e -> {
                    log.error("Error updating Ad: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to update Ad"));
                });
    }

    public Mono<Ad> delete(Ad ad) {
        return r2dbcEntityTemplate.select(Ad.class)
                .matching(Query.query(Criteria.where("id").is(ad.getId())))
                .one()
                .switchIfEmpty(Mono.error(new RuntimeException("Ad not found")))
                .flatMap(existingAd ->
                        r2dbcEntityTemplate.delete(existingAd)
                                .thenReturn(existingAd)
                )
                .as(tx::transactional)
                .onErrorResume(e -> {
                    log.error("Error deleting Ad: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Failed to delete Ad"));
                });
    }
}



