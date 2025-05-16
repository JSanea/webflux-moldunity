package web.app.webflux_moldunity.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.entity.ad.Subcategory;
import web.app.webflux_moldunity.enums.AdSubtype;
import web.app.webflux_moldunity.service.AdService;
import web.app.webflux_moldunity.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@Slf4j
public class AdController {
    private final AdService adService;
    private final UserService userService;


    @Tag(name = "Advertisements", description = "Endpoints for managing advertisements")
    @Operation(
            summary = "Get advertisement by ID",
            description = "Fetches a specific advertisement by its ID and subtype.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the advertisement", required = true),
                    @Parameter(name = "type", description = "Subtype of the advertisement", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ad successfully retrieved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ad.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ad not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error"
                    )
            }
    )
    @GetMapping(value = "/ads/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Ad>> getById(@PathVariable Long id,
                                            @RequestParam(value = "type") String subtype) {
        return Mono.justOrEmpty(AdSubtype.fromSubcategoryName(subtype))
                .flatMap(sub -> adService.getById(id, sub.getSubcategoryType()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(e -> {
                    log.error("Error fetching Ad: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping(value = "/ads/flux/{subcategory}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ResponseEntity<Ad>> findBySubcategory(@PathVariable String subcategory,
                                                      @RequestParam Long page,
                                                      @RequestParam(required = false) Map<String, List<String>> filter){
        return Mono.justOrEmpty(AdSubtype.fromSubcategoryName(subcategory))
                .flatMapMany(sub -> adService.findBySubcategory(sub.getSubcategoryName(), page, filter))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error fetching Ad: ", e);
                    return Flux.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping(value = "/ads/list/{subcategory}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Ad>>> getBySubcategory(@PathVariable String subcategory,
                                                           @RequestParam Long page,
                                                           @RequestBody(required = false) Map<String, List<String>> filter){
        return Mono.justOrEmpty(AdSubtype.fromSubcategoryName(subcategory))
                .flatMap(sub -> adService.getBySubcategory(sub.getSubcategoryName(), page, filter))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error fetching Ad: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping(value = "/ads/{adId}/subcategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<? extends Subcategory> getSubcategoryByAdId(@PathVariable Long adId,
                                                            @RequestParam("type") String subtype){
        return Mono.justOrEmpty(AdSubtype.fromSubcategoryName(subtype))
                .flatMap(sub -> adService.findSubcategoryByAdId(adId, sub.getSubcategoryType()))
                .onErrorResume(e -> {
                    log.error("Error to fetch subcategory by Ad Id: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

    @GetMapping(value = "/ads/metadata/{subcategory}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String, Long>>> getSubcategoryMetadata(@PathVariable String subcategory) {
        return Mono.justOrEmpty(AdSubtype.fromSubcategoryName(subcategory))
                .flatMap(sub -> adService.countBySubcategory(sub.getSubcategoryName()))
                .map(total ->{
                    Map<String, Long> metadata = new HashMap<>();
                    metadata.put("total", total);
                    metadata.put("size", 50L);
                    return ResponseEntity.ok(metadata);
                })
                .onErrorResume(e -> {
                    log.error("Error fetching Ad metadata", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }


    @GetMapping(value = "/count/ads/{username}")
    public Mono<ResponseEntity<Long>> getCount(@PathVariable String username){
        return adService.getCountAdsByUsername(username)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body(0L)))
                .onErrorResume(e -> {
                    log.error("Error get count: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Tag(name = "Advertisements", description = "Endpoints for managing advertisements")
    @Operation(
            summary = "Create a new advertisement",
            description = "Adds a new advertisement with a specific subcategory.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Ad successfully created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ad.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Missing subcategory or invalid type"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error"
                    )
            }
    )
    @PostMapping(value = "/ads", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Ad>> add(@RequestBody Ad ad) {
        if (ad.getSubcategory() == null)
            return Mono.just(ResponseEntity.badRequest().build());

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (UserDetails) ctx.getAuthentication().getPrincipal())
                .map(UserDetails::getUsername)
                .flatMap(username -> adService.getCountAdsByUsername(username)
                        .filter(count -> count < 20)
                        .flatMap(validUser -> userService.findUserByName(username))
                        .flatMap(user -> {
                            ad.setUsername(username);
                            ad.setUserId(user.getId());

                            if(ad.getCountry() == null)
                                ad.setCountry(user.getCountry());

                            if(ad.getLocation() == null)
                                ad.setLocation(user.getLocation());

                            return Mono.justOrEmpty(AdSubtype.fromSubcategoryName(ad.getSubcategoryName()));
                        })
                        .flatMap(subcategory -> adService.save(ad, subcategory.getSubcategoryType()))
                        .map(savedAd -> ResponseEntity.status(HttpStatus.CREATED).body(savedAd))
                )
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> {
                    log.error("Error saving Ad: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PutMapping(value = "/ads",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Ad>> update(@RequestBody Ad ad) {
        if (ad.getSubcategory() == null)
            return Mono.just(ResponseEntity.badRequest().build());

        return adService.isOwner(ad.getId())
                .flatMap(owner -> owner
                        ? Mono.justOrEmpty(AdSubtype.fromSubcategoryName(ad.getSubcategoryName()))
                        : Mono.empty())
                .flatMap(subcategory -> adService.update(ad, subcategory.getSubcategoryType()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(e -> {
                    log.error("Error updating Ad: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @DeleteMapping(value = "/ads",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Ad>> delete(@RequestBody Ad ad) {
        return adService.isOwner(ad.getId())
                .flatMap(owner -> owner ? adService.delete(ad) : Mono.empty())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build()))
                .onErrorResume(e -> {
                    log.error("Error deleting Ad: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}









