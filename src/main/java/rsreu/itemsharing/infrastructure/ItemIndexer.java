package rsreu.itemsharing.infrastructure;

import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.repositories.ItemRepository;
import rsreu.itemsharing.repositories.ItemSearchRepository;

import java.util.List;

@Service
public class ItemIndexer {

    private final ItemRepository itemRepository;
    private final ItemSearchRepository itemSearchRepository;

    public ItemIndexer(ItemRepository itemRepository, ItemSearchRepository itemSearchRepository) {
        this.itemRepository = itemRepository;
        this.itemSearchRepository = itemSearchRepository;
    }

    public void reindexAllItems() {
        List<Item> allItems = itemRepository.findAll();
        List<ItemDocument> documents = allItems.stream()
                .map(item -> new ItemDocument(
                        item.getItemId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAddress(),
                        item.getColor() != null ? item.getColor().getName() : null,
                        item.getMaterial() != null ? item.getMaterial().getName() : null,
                        item.getMaker() != null ? item.getMaker().getName() : null,
                        item.getModel() != null ? item.getModel().getName() : null
                ))
                .toList();

        itemSearchRepository.saveAll(documents);
    }
}