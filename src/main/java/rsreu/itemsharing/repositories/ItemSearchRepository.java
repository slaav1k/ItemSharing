package rsreu.itemsharing.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import rsreu.itemsharing.infrastructure.ItemDocument;

import java.util.List;

public interface ItemSearchRepository extends ElasticsearchRepository<ItemDocument, String> {
    List<ItemDocument> findByNameContaining(String text);
}
