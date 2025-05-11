package rsreu.itemsharing.repositories;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import rsreu.itemsharing.infrastructure.ItemDocument;

import java.util.List;

public interface ItemSearchRepository extends ElasticsearchRepository<ItemDocument, String> {
    List<ItemDocument> findByNameContaining(String name);

    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\", \"description\", \"address\", \"color\", \"material\", \"maker\", \"model\"]}}")
    List<ItemDocument> searchByMultipleFields(String query);
}