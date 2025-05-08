package web.app.webflux_moldunity.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.entity.ad.AdImage;
import web.app.webflux_moldunity.entity.ad.AdWithImage;
import web.app.webflux_moldunity.entity.ad.Subcategory;
import web.app.webflux_moldunity.exception.AdServiceException;
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

    public <S extends Subcategory> Mono<Ad> getById(Long id, Class<S> subcategoryType) {
        return r2dbcEntityTemplate.selectOne(Query.query(Criteria.where("id").is(id)), Ad.class)
                .flatMap(ad -> r2dbcEntityTemplate
                        .selectOne(Query.query(Criteria.where("ad_id").is(ad.getId())), subcategoryType)
                        .map(subcategory -> {
                            ad.setSubcategory(subcategoryType.cast(subcategory));
                            return ad;
                        })
                )
                .flatMap(adWithSubcategory -> r2dbcEntityTemplate
                        .select(Query.query(Criteria.where("ad_id").is(adWithSubcategory.getId())), AdImage.class)
                        .collectList()
                        .map(images -> {
                            adWithSubcategory.setAdImages(images);
                            return adWithSubcategory;
                        })
                )
                .onErrorResume(e -> {
                    log.error("Error get Ad by id: {}", e.getMessage(), e);
                    return Mono.error(new AdServiceException("Failed to get Ad by id"));
                });
    }

    public <S extends Subcategory> Mono<S> findSubcategoryByAdId(Long adId, Class<S> subcategory){
        return r2dbcEntityTemplate.selectOne(
                Query.query(Criteria.where("ad_id").is(adId)),
                subcategory
        );
    }

    public Flux<Ad> findBySubcategoryDescByRepublishedAt(String subcategory, Long page){
        String sql = baseSelectAdsWithImages() +
                """
                WHERE ads.subcategory_name = :subcategory
                ORDER BY ads.republished_at DESC
                LIMIT :limit OFFSET :offset
                """;

        long limit = 50;
        return findAdsByCondition(databaseClient.sql(sql)
                .bind("subcategory", subcategory)
                .bind("limit", limit)
                .bind("offset", limit * (Math.max(page, 1L) - 1)));
    }

    public Flux<Ad> findByUsername(String username){
        String sql = baseSelectAdsWithImages() +
                """
                WHERE ads.username = :username
                """;

        return findAdsByCondition(databaseClient.sql(sql)
                .bind("username", username));
    }

    public Mono<List<Ad>> getBySubcategoryDescByRepublishedAt(String subcategory, Long page){
        return findBySubcategoryDescByRepublishedAt(subcategory, page).collectList();
    }

    public Mono<List<Ad>> getByUsername(String username) {
        return findByUsername(username).collectList();
    }

    public Mono<Long> getCountAdsByUsername(String username) {
        return databaseClient.sql("SELECT count(*) AS cnt FROM ads WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> row.get("cnt", Long.class))
                .one()
                .onErrorResume(e -> {
                    log.error("Error counting Ads by username: {}", e.getMessage(), e);
                    return Mono.error(new AdServiceException("Failed to count Ads by username"));
                });
    }

    public Mono<String> getOwnerById(Long id){
        return databaseClient.sql("SELECT ads.username FROM ads WHERE ads.id = :id")
                .bind("id", id)
                .map(((row, rowMetadata) -> row.get("username", String.class)))
                .one()
                .onErrorResume(e -> {
                    log.error("Error to get Ad owner: {}", e.getMessage(), e);
                    return Mono.error(new AdServiceException("Error to get Ad owner"));
                });
    }

    public Mono<Long> countBySubcategory(String subcategory) {
        String sql = "SELECT COUNT(*) FROM ads WHERE subcategory_name = :subcategory";
        return databaseClient.sql(sql)
                .bind("subcategory", subcategory)
                .map(row -> row.get(0, Long.class))
                .one();
    }

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
                    return Mono.error(new AdServiceException("Failed to insert Ad"));
                });
    }

    public Mono<AdImage> saveImageUrl(AdImage image) {
        return r2dbcEntityTemplate
                .insert(image)
                .as(tx::transactional)
                .onErrorResume(e -> {
                    log.error("Error saving image url: {}", e.getMessage(), e);
                    return Mono.error(new AdServiceException("Failed to save image url"));
                });
    }

    public <S extends Subcategory> Mono<Ad> update(Ad ad, Class<S> subcategory) {
        return r2dbcEntityTemplate.select(Ad.class)
                .matching(Query.query(Criteria.where("id").is(ad.getId())))
                .one()
                .switchIfEmpty(Mono.error(new AdServiceException("Ad not found")))
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
                    return Mono.error(new AdServiceException("Failed to update Ad"));
                });
    }

    public Mono<Ad> delete(Ad ad) {
        return r2dbcEntityTemplate.select(Ad.class)
                .matching(Query.query(Criteria.where("id").is(ad.getId())))
                .one()
                .switchIfEmpty(Mono.error(new AdServiceException("Ad not found")))
                .flatMap(existingAd ->
                        r2dbcEntityTemplate.delete(existingAd)
                                .thenReturn(existingAd)
                )
                .as(tx::transactional)
                .onErrorResume(e -> {
                    log.error("Error deleting Ad: {}", e.getMessage(), e);
                    return Mono.error(new AdServiceException("Failed to delete Ad"));
                });
    }

    public Mono<Boolean> isOwner(Long adId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> getOwnerById(adId).map(owner -> owner.equals(auth.getPrincipal())))
                .onErrorResume(e -> {
                    log.error("Error to check Ad owner: {}", e.getMessage(), e);
                    return Mono.error(new AdServiceException("Error to check Ad owner"));
                });
    }


    private String baseSelectAdsWithImages(){
        return String.format("""
                %1$s FROM ads
                LEFT JOIN ad_images ON ad_images.ad_id = ads.id
                """, selectAdsWithImages());
    }

    private Flux<Ad> findAdsByCondition(DatabaseClient.GenericExecuteSpec executeSpec) {
        return executeSpec.map((row, metadata) -> {
                    Ad ad = Ad.mapRowToAd(row);
                    AdImage adImage = AdImage.mapRowToAdImage(row);
                    return new AdWithImage(ad, adImage);
                })
                .all()
                .collectMultimap(a -> a.ad().getId())
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> {
                    List<AdWithImage> adWithImages = new ArrayList<>(entry.getValue());
                    Ad ad = adWithImages.get(0).ad();

                    List<AdImage> images = entry.getValue().stream()
                            .map(AdWithImage::adImage)
                            .filter(adImage -> adImage.getId() != null && adImage.getUrl() != null)
                            .toList();

                    ad.setAdImages(images);
                    return ad;
                })
                .onErrorResume(e -> {
                    log.error("Error fetching Ads: {}", e.getMessage(), e);
                    return Flux.error(new AdServiceException("Failed to fetch Ads"));
                });
    }

    private String selectAdsWithImages(){
        return """
                SELECT ads.id AS ads_id, ads.username, ads.offer_type, ads.title, ads.category_name, ads.subcategory_name,
                   ads.country, ads.location, ads.description, ads.price, ads.created_at AS ads_created_at, ads.updated_at,
                   ads.republished_at, ads.user_id,
                   ad_images.id AS ad_images_id, ad_images.url AS ad_images_url, ad_images.created_at AS ad_images_created_at,
                   ad_images.ad_id AS ad_images_ad_id
                """;
    }
}



