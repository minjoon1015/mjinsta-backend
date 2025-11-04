package back_end.springboot.component;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class FileManager {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public String uploadFile(MultipartFile file, String folderName) {
        try {
            String fileName = UUID.randomUUID().toString();
            String key = folderName + "/" + fileName;

            InputStream inputStream = file.getInputStream();
            long contentLength = file.getSize();
            String contentType = file.getContentType();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));

            String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
            return fileUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteFile(String url) {
        try {
            String fileUrlPrefix = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
            if (!url.startsWith(fileUrlPrefix))
                return false;

            String key = url.substring(fileUrlPrefix.length());

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
