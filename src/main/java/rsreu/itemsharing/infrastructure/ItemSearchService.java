package rsreu.itemsharing.infrastructure;

import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.repositories.ItemSearchRepository;

import java.util.List;

@Service
public class ItemSearchService {
    private final ItemSearchRepository repository;

    public ItemSearchService(ItemSearchRepository repository) {
        this.repository = repository;
    }

    public void index(Item item) {
        ItemDocument doc = new ItemDocument(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getAddress()
        );
        repository.save(doc);
    }

    public List<ItemDocument> search(String keyword) {
        return repository.findByNameContaining(keyword);
    }
}

