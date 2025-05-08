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
import web.app.webflux_moldunity.service.AdService;
import web.app.webflux_moldunity.service.image.ImageService;
import web.app.webflux_moldunity.util.FilePartUtil;
import web.app.webflux_moldunity.util.ImageUtil;

import java.util.List;


@RestController
@AllArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;
    private final AdService adService;

    @PostMapping(value = "/images/ads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> save(@PathVariable Long id, @RequestPart("images") Flux<FilePart> images){
        return images
        .collectList()
        .flatMap(fileList -> {
            if (fileList.size() > 10) {
                return Mono.just(ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("You can upload a maximum of 10 images"));
            }

            return Flux.fromIterable(fileList)
                .flatMap(filePart -> FilePartUtil.filePartToFile(filePart)
                        .flatMap(file -> ImageUtil.isWebP(file)
                                .flatMap(isWebP -> {
                                    if (!isWebP) {
                                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body("Only WebP images are allowed"));
                                    }
                                        return imageService.saveWebp(id, filePart.filename(), file);
                                    })
                            )
                )
                .then(Mono.just(ResponseEntity.ok("Images uploaded successfully")));
                })
                .onErrorResume(e -> {
                    log.error("Error uploading images: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images"));
                });
    }

    @PostMapping(value = "/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> save(@PathVariable Long id,
                                             @RequestPart("images") List<FilePart> images){
        if(images.size() > 10){
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Max 10 images allowed"));
        }

        return null;
//        return Flux.fromIterable(images)
//                .flatMap(ImageUtil::filePartToFile)
//                .map(f -> ImageUtil.isWebP(f))
//                .map()
    }
}
