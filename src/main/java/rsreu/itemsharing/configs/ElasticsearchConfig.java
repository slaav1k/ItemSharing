package rsreu.itemsharing.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import rsreu.itemsharing.infrastructure.ItemDocument;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
    }

    @Bean
    @Override
    public ElasticsearchOperations elasticsearchOperations(ElasticsearchConverter converter,
                                                           ElasticsearchClient client) {
        ElasticsearchOperations operations = super.elasticsearchOperations(converter, client);

        if (!operations.indexOps(ItemDocument.class).exists()) {
            operations.indexOps(ItemDocument.class).create();        // создаёт индекс
            operations.indexOps(ItemDocument.class).putMapping();    // использует аннотации
        }

        return operations;
    }
}
