package web.app.webflux_moldunity.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.dto.ad.AdDetails;
import web.app.webflux_moldunity.dto.ad.AdDetailsWithImages;
import web.app.webflux_moldunity.dto.ad.AdPage;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.entity.ad.Subcategory;
import web.app.webflux_moldunity.enums.AdSubtype;
import web.app.webflux_moldunity.service.AdService;
import web.app.webflux_moldunity.service.UserService;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api")
public class AdController {
    @Value("${ads.subcategory.limit}")
    private Long limit;
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
    public Mono<ResponseEntity<AdDetailsWithImages>> getById(@PathVariable Long id,
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

    @PostMapping(value = "/ads/{subcategory}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AdPage>> getBySubcategory(@PathVariable String subcategory,
                                                         @RequestParam Long page,
                                                         @RequestBody(required = false) Map<String, List<String>> filter){
        return Mono.justOrEmpty(AdSubtype.fromSubcategoryName(subcategory))
                .flatMap(sub -> adService.findBySubcategoryAndFilter(sub.getSubcategoryName(), page, filter))
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

    @GetMapping(value = "/count/{subcategory}")
    public Mono<ResponseEntity<Long>> getCount(@PathVariable String subcategory){
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (String)ctx.getAuthentication().getPrincipal())
                .flatMap(username -> {
                    var adSubtype = AdSubtype.fromSubcategoryName(subcategory);
                    return adSubtype.isPresent() ?
                            adService.getCountAdsByUsernameAndSubcategory(username, adSubtype.get().getSubcategoryName())
                            : Mono.empty();
                })
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .onErrorResume(e -> {
                    log.error("Error to count Ads by username and subcategory: {}", e.getMessage(), e);
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
    @PostMapping(value = "/ads",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AdDetails>> add(@Valid @RequestBody AdDetails adDetails) {
        if (adDetails.subcategory() == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (String)ctx.getAuthentication().getPrincipal())
                .flatMap(username -> adService.getCountAdsByUsernameAndSubcategory(username, adDetails.ad().getSubcategoryName())
                        .filter(count -> count < limit)
                        .flatMap(validUser -> userService.findUserByName(username))
                        .flatMap(user -> {
                            adDetails.ad().setUsername(username);
                            adDetails.ad().setUserId(user.getId());

                            if(adDetails.ad().getCountry() == null)
                                adDetails.ad().setCountry(user.getCountry());

                            if(adDetails.ad().getLocation() == null)
                                adDetails.ad().setLocation(user.getLocation());

                            return Mono.justOrEmpty(AdSubtype.fromSubcategoryName(adDetails.ad().getSubcategoryName()));
                        })
                        .flatMap(subcategory -> adService.save(adDetails, subcategory.getSubcategoryType()))
                        .map(savedAd -> ResponseEntity.status(HttpStatus.OK).body(savedAd))
                )
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> {
                    log.error("Error saving Ad: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping(value = "/republish/ads/{id}")
    public Mono<ResponseEntity<Ad>> republish(@PathVariable Long id){
        return adService.republish(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> {
                    log.error("Error to republish Ad: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PutMapping(value = "/edit/ads/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Ad>> update(@PathVariable Long id,
                                           @Valid @RequestBody AdDetails adDetails) {
        if (adDetails.subcategory() == null || !id.equals(adDetails.ad().getId())) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return  Mono.justOrEmpty(AdSubtype.fromSubcategoryName(adDetails.ad().getSubcategoryName()))
                .flatMap(subcategory -> adService.update(adDetails, subcategory.getSubcategoryType()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(e -> {
                    log.error("Error updating Ad: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @DeleteMapping(value = "/edit/ads/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Ad>> delete(@PathVariable Long id) {
        return adService.delete(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build()))
                .onErrorResume(e -> {
                    log.error("Error deleting Ad: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}









