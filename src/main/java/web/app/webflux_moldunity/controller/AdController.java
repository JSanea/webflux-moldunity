package web.app.webflux_moldunity.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.ad.Ad;
import web.app.webflux_moldunity.enums.AdType;
import web.app.webflux_moldunity.service.AdService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import web.app.webflux_moldunity.service.UserService;


@RestController
@Slf4j
public class AdController {
    private final AdService adService;
    private final UserService userService;

    @Autowired
    public AdController(AdService adService, UserService userService) {
        this.adService = adService;
        this.userService = userService;
    }

    @GetMapping(value = "/ads/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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
    public Mono<ResponseEntity<Ad>> getById(@PathVariable Long id, @RequestParam(value = "type") String subtype) {
        return Mono.justOrEmpty(AdType.fromSubcategoryName(subtype))
                .flatMap(sub -> adService.getById(id, sub.getSubcategoryType()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(e -> {
                    log.error("Error fetching Ad: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping(value = "/ads", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Advertisements", description = "Endpoints for managing advertisements")
    @Operation(
            summary = "Create a new advertisement",
            description = "Adds a new advertisement with a specific subcategory.",
            requestBody = @RequestBody(
                    description = "Ad object to be created",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ad.class))
            ),
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
    public Mono<ResponseEntity<Ad>> add(@Valid @RequestBody Ad ad) {
        if (ad.getSubcategory() == null)
            return Mono.just(ResponseEntity.badRequest().build());

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (UserDetails) ctx.getAuthentication().getPrincipal())
                .map(UserDetails::getUsername)
                .filter(authenticatedUsername -> authenticatedUsername.equals(ad.getUsername()))
                .flatMap(username -> adService.getCountAdsByUsername(username)
                        .filter(count -> count < 10)
                        .flatMap(validUser -> userService.getIdByUsername(username))
                        .flatMap(userId -> {
                            ad.setUserId(userId);
                            return Mono.justOrEmpty(AdType.fromSubcategoryName(ad.getSubcategoryName()));
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
}









