package web.app.webflux_moldunity.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.Ad;
import web.app.webflux_moldunity.enums.AdType;
import web.app.webflux_moldunity.service.AdService;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class AdController {
    private final AdService adService;

    @Autowired
    public AdController(AdService adService) {
        this.adService = adService;
    }

    @Operation(summary = "Get ad by ID", description = "Retrieves an ad with given ID and subtype")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ad found"),
            @ApiResponse(responseCode = "404", description = "Ad not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/ads/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Ad>> getById(@PathVariable Long id, @RequestParam(value = "type") String subtype) {
        return Mono.justOrEmpty(AdType.fromSubcategoryName(subtype))
                .flatMap(sub -> adService.getById(id, sub.getCategoryType(), sub.getSubcategoryType()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(e -> {
                    log.error("Error fetching Ad: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping(value = "/ads", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Ad>> add(@RequestBody Ad ad, @RequestParam(value = "type") String subtype) {
        if (ad.getCategory() == null || ad.getCategory().getSubcategory() == null) {
            return Mono.just(ResponseEntity.badRequest().body(new Ad()));
        }

        return Mono.justOrEmpty(AdType.fromSubcategoryName(subtype))
                .flatMap(sub -> adService.save(ad, sub.getCategoryType(), sub.getSubcategoryType())
                .map(savedAd -> ResponseEntity.status(HttpStatus.CREATED).body(savedAd)))
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> {
                    log.error("Error save Ad: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}
