package web.app.webflux_moldunity.service.image;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface ReactiveUploadImageService {
    Mono<String> upload(FilePart filePart, Long adId);
}
