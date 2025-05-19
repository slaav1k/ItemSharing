package rsreu.itemsharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import rsreu.itemsharing.configs.ObjectS3Config;

import javax.sql.DataSource;


@SpringBootApplication
@EnableConfigurationProperties(ObjectS3Config.class)
@EnableCaching
@EnableAsync
@EnableAspectJAutoProxy
public class ItemSharingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemSharingApplication.class, args);
    }

    @Bean
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
                .type(org.postgresql.ds.PGSimpleDataSource.class)
                .url("jdbc:postgresql://localhost:5433/ItemSharingBD")
                .username("postgres")
                .password("123")
                .build();
    }


}
