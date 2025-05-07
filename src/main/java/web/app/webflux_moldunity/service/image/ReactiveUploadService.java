package web.app.webflux_moldunity.service.image;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;

public interface ReactiveUploadService {
    default Mono<String> upload(Long adId, FilePart filePart, AsyncRequestBody asyncRequestBody){
        return Mono.empty();
    }

    default Mono<String> upload(Long adId, FilePart filePart){
        return Mono.empty();
    }
}
