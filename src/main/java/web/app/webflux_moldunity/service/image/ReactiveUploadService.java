package web.app.webflux_moldunity.service.image;

import reactor.core.publisher.Mono;

import java.io.File;

public interface ReactiveUploadService {
    default Mono<String> upload(Long adId,  File file){
        return Mono.empty();
    }
}
