package rsreu.itemsharing.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rsreu.itemsharing.configs.ObjectS3Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private AmazonS3 s3;
    private final ObjectS3Config s3Config;

    private void initializeS3Client() {
        if (s3 == null) {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(s3Config.getAccessKey(), s3Config.getSecretKey());
            s3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withEndpointConfiguration(
                            new AmazonS3ClientBuilder.EndpointConfiguration(
                                    "storage.yandexcloud.net", "ru-central1"
                            )
                    )
                    .build();
        }
    }

    public String uploadFile(String fileName, MultipartFile file) {
        return uploadFile(fileName, file, "items/");
    }

    public String uploadFile(String fileName, MultipartFile file, String folder) {
        initializeS3Client();
        String key = folder + fileName;
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3.putObject(s3Config.getBucketName(), key, inputStream, metadata);
            return getFileUrl(key);
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", key, e);
            throw new RuntimeException("Failed to upload file to S3: " + key, e);
        }
    }


    public void deleteFile(String key) {
        initializeS3Client();
        try {
            s3.deleteObject(s3Config.getBucketName(), key);
            log.info("Deleted file from S3: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}", key, e);
            throw new RuntimeException("Failed to delete file from S3: " + key, e);
        }
    }

    public String getFileUrl(String key) {
        initializeS3Client();
        URL url = s3.getUrl(s3Config.getBucketName(), key);
        return url.toString();
    }
}