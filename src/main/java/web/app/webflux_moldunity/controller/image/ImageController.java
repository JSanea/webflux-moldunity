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
import web.app.webflux_moldunity.service.image.ImageService;
import web.app.webflux_moldunity.util.FilePartUtil;

import java.util.List;


@RestController
@AllArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value = "/flux/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> save(@PathVariable Long id,
                                             @RequestPart("images") Flux<FilePart> images){
        return images
                .flatMap(FilePartUtil::filePartToFile)
                .flatMap(imageService::convertImage)
                .flatMap(webp -> imageService.saveWebp(id, webp))
                .then(Mono.just(ResponseEntity.ok("Files uploaded successfully")))
                .onErrorResume(e -> {
                    log.error("Error uploading images: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping(value = "/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<List<AdImage>>> save(@PathVariable Long id,
                                                    @RequestPart("images") List<FilePart> images){
        if (images.isEmpty() || images.size() > 10) {
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build());
        }

        return FilePartUtil.filePartsToFiles(images)
                .flatMap(imageService::convertImages)
                .flatMapMany(Flux::fromIterable)
                .flatMap(webpFile -> imageService.saveWebp(id, webpFile))
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error uploading images: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}

















