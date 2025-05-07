package web.app.webflux_moldunity.service.image.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import web.app.webflux_moldunity.service.image.ReactiveUploadService;

import java.util.UUID;

@Service
@Slf4j
public class S3UploadService implements ReactiveUploadService {
    @Value("${aws.s3.bucket}")
    private String bucket;
    private final S3AsyncClient asyncS3Client;

    public S3UploadService(S3AsyncClient asyncS3Client) {
        this.asyncS3Client = asyncS3Client;
    }

    @Override
    public Mono<String> upload(Long adId, FilePart filePart, AsyncRequestBody asyncRequestBody){
        String key = "ads/" + adId + "/" + UUID.randomUUID() + "_" + filePart.filename();
        return Mono.fromFuture(asyncS3Client.putObject(
                getPutObjectRequest(filePart, key, adId),
                asyncRequestBody)
        ).thenReturn("https://" + bucket + ".s3.amazonaws.com/" + key);
    }


    private PutObjectRequest getPutObjectRequest(FilePart filePart, String key, Long adId){
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(filePart.headers().getContentType().toString())
                .build();
    }
}




