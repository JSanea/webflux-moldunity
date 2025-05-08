package web.app.webflux_moldunity.service.image;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import web.app.webflux_moldunity.entity.ad.AdImage;
import web.app.webflux_moldunity.service.AdService;

import java.io.File;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class ImageService {
    private final ReactiveUploadService s3UploadService;
    private final AdService adService;

    public Mono<AdImage> saveWebp(Long id, String filename, File file){
        // convert file to webp
        return s3UploadService.upload(id, file)
                .flatMap(url -> adService.saveImageUrl(AdImage.builder()
                        .url(url)
                        .createdAt(LocalDateTime.now())
                        .adId(id)
                        .build()))
                .doFinally(signalType -> {
                    if(!file.delete()){
                        log.warn("Failed to delete temp input file: {}", file.getAbsolutePath());
                        file.deleteOnExit();
                    }
                    // delete webp
                });

    }
}
