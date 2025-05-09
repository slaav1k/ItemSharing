package rsreu.itemsharing.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Getter
@Setter
@ConfigurationProperties(prefix = "object-s3")
@Primary
public class ObjectS3Config {
    private final String bucketName = "item.sharing";
    private String accessKey;
    private String secretKey;
}
