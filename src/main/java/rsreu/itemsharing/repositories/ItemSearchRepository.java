package rsreu.itemsharing.repositories;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import rsreu.itemsharing.infrastructure.ItemDocument;

import java.util.List;

public interface ItemSearchRepository extends ElasticsearchRepository<ItemDocument, String> {
    List<ItemDocument> findByNameContaining(String name);

    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^10\", \"description^5\", \"address^1\", \"color^1\", \"material^1\", \"maker^1\", \"model^1\", \"customAttributes.*^1\"]}}")
    List<ItemDocument> searchByMultipleFields(String query);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^10\", \"description^5\", \"color^1\", \"material^1\", \"maker^1\", \"model^1\", \"customAttributes.*^1\"], \"minimum_should_match\": \"1\"}}, {\"wildcard\": {\"address\": \"*?1*\"}}]}}")
    List<ItemDocument> searchByQueryAndCity(String query, String city);

    @Query("{\"wildcard\": {\"address\": \"*?0*\"}}")
    List<ItemDocument> findByCity(String city);
}