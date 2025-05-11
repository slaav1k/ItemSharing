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
                item.getAddress(),
                item.getColor() != null ? item.getColor().getName() : null,
                item.getMaterial() != null ? item.getMaterial().getName() : null,
                item.getMaker() != null ? item.getMaker().getName() : null,
                item.getModel() != null ? item.getModel().getName() : null
        );
        repository.save(doc);
    }

    public List<ItemDocument> search(String keyword) {
        return repository.searchByMultipleFields(keyword);
    }
}