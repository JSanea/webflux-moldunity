package web.app.webflux_moldunity.controller.image;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.ad.AdImage;
import web.app.webflux_moldunity.service.AdService;
import web.app.webflux_moldunity.service.image.s3.S3UploadService;

import java.time.LocalDateTime;


@RestController
@AllArgsConstructor
@Slf4j
public class ImageController {
    private final S3UploadService s3UploadService;
    private final AdService adService;

    @PostMapping(value = "/images/ads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> save(@PathVariable Long id, @RequestPart("images") Flux<FilePart> images){
        return images
            .flatMap(file -> s3UploadService.upload(file, id))
            .flatMap(url -> adService.saveImageUrl(AdImage.builder()
                    .url(url)
                    .createdAt(LocalDateTime.now())
                    .adId(id)
                    .build()))
            .collectList()
            .map(adImages -> ResponseEntity.ok("Images uploaded successfully: " + adImages.toString()))
            .onErrorResume(e -> {
                log.error("Error uploading images: {}", e.getMessage(), e);
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images"));
            });
    }
}
