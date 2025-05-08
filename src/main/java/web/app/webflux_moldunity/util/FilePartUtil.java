package web.app.webflux_moldunity.util;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

public class FilePartUtil {
    public static Mono<File> filePartToFile(FilePart filePart) {
        return Mono.fromCallable(() -> File.createTempFile("upload-", "-" + filePart.filename()))
                .flatMap(tempFile -> filePart.transferTo(tempFile).thenReturn(tempFile))
                .onErrorMap(IOException.class, e -> new RuntimeException("Failed to create temp file or transfer data", e));
    }
}
