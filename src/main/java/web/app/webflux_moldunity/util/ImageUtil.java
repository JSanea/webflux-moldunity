package web.app.webflux_moldunity.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


@Service
@Slf4j
public class ImageUtil {
    public static Mono<Boolean> isWebPFormat(File file) {
        return Mono.fromCallable(() -> {
                    try (InputStream inputStream = new FileInputStream(file)) {
                        byte[] header = new byte[8];
                        int bytesRead = inputStream.read(header);

                        if (bytesRead != 8) {
                            return false;
                        }

                        if (header[0] != 'R' || header[1] != 'I' || header[2] != 'F' || header[3] != 'F') {
                            return false;
                        }

                        return header[4] == 'W' && header[5] == 'E' && header[6] == 'B' && header[7] == 'P';
                    } catch (IOException e) {
                        return false;
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public static Mono<File> FilePartToFile(FilePart filePart) {
        try {
            File tempFile = File.createTempFile("upload-", "-" + filePart.filename());
            return filePart.transferTo(tempFile).thenReturn(tempFile);
        } catch (IOException e) {
            return Mono.error(new RuntimeException("Could not create temp file", e));
        }
    }
}
