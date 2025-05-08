package web.app.webflux_moldunity.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Service
@Slf4j
public class ImageUtil {
    public static Mono<Boolean> isWebP(File file) {
        return Mono.fromCallable(() -> {
                    try (InputStream inputStream = new FileInputStream(file)) {
                        byte[] header = new byte[12];
                        int bytesRead = inputStream.read(header);
                        if (bytesRead < 12) {
                            return false;
                        }

                        if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F') {
                            if (header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
                                return true;
                            }
                        }
                    } catch (IOException e) {
                        log.error("Error to check if file is Webp: {}", e.getMessage(), e);
                    }
                    return false;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public static Mono<Boolean> reactiveIsHEIC(File file){
        return Mono.fromCallable(() -> isHEIC(file))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public static boolean isHEIC(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[20];
            int bytesRead = fis.read(buffer);
            if (bytesRead < 20) {
                return false;
            }

            String boxType = new String(buffer, 4, 4, StandardCharsets.US_ASCII);
            if (!"ftyp".equals(boxType)) {
                return false;
            }

            String brand = new String(buffer, 8, 4, StandardCharsets.US_ASCII);

            String[] heicBrands = {"heic", "heix", "mif1", "msf1", "hevc", "mshp"};
            for (String b : heicBrands) {
                if (brand.startsWith(b)) {
                    return true;
                }
            }
        } catch (IOException e) {
            log.error("Error to check if file is HEIC: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * Checks if the given file is a JPEG or JPG based on its magic number.
     *
     * @param file the file to check
     * @return true if the file is a JPEG/JPG, false otherwise
     * @throws IOException if an I/O error occurs
     */
    public static boolean isJPEG(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[2];
            int bytesRead = fis.read(header);

            if (bytesRead != 2) {
                return false;
            }

            return (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8;
        }catch (IOException e){
            log.error("Error to check if file is JPEG: {}", e.getMessage(), e);
        }

        return false;
    }

    /**
     * Checks if the given file is a PNG based on its magic number.
     *
     * @param file the file to check
     * @return true if the file is a PNG, false otherwise
     * @throws IOException if an I/O error occurs
     */
    public static boolean isPNG(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }

        byte[] pngSignature = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A
        };

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int bytesRead = fis.read(header);

            if (bytesRead != 8) {
                return false;
            }

            for (int i = 0; i < pngSignature.length; i++) {
                if (header[i] != pngSignature[i]) {
                    return false;
                }
            }
            return true;
        }catch (IOException e){
            log.error("Error to check if file is PNG: {}", e.getMessage(), e);
        }

        return false;
    }
}
