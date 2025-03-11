package rsreu.itemsharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;


@SpringBootApplication
public class ItemSharingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemSharingApplication.class, args);
    }

    @Bean
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
                .type(org.postgresql.ds.PGSimpleDataSource.class)
                .url("jdbc:postgresql://localhost:5432/ItemSharingBD")
                .username("postgres")
                .password("123")
                .build();
    }


}
