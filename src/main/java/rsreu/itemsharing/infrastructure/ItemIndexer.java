package rsreu.itemsharing.infrastructure;

import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.Attribute;
import rsreu.itemsharing.entities.AttributeType;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemAttribute;
import rsreu.itemsharing.repositories.AttributeRepository;
import rsreu.itemsharing.repositories.ItemAttributeRepository;
import rsreu.itemsharing.repositories.ItemRepository;
import rsreu.itemsharing.repositories.ItemSearchRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemIndexer {

    private final ItemRepository itemRepository;
    private final ItemSearchRepository itemSearchRepository;
    private final ItemAttributeRepository itemAttributeRepository;
    private final AttributeRepository attributeRepository;

    public ItemIndexer(ItemRepository itemRepository, ItemSearchRepository itemSearchRepository,
                       ItemAttributeRepository itemAttributeRepository, AttributeRepository attributeRepository) {
        this.itemRepository = itemRepository;
        this.itemSearchRepository = itemSearchRepository;
        this.itemAttributeRepository = itemAttributeRepository;
        this.attributeRepository = attributeRepository;
    }

    public void reindexAllItems() {
        List<Item> allItems = itemRepository.findAll();
        List<ItemDocument> documents = allItems.stream()
                .map(item -> {
                    List<ItemAttribute> itemAttributes = itemAttributeRepository.findById_Item(item.getItemId());
                    Map<String, String> customAttributes = new HashMap<>();
                    for (ItemAttribute attr : itemAttributes) {
                        Long attributeId = attr.getId().getAttribute();
                        Attribute attribute = attributeRepository.findByAttributeId(attributeId);
                        if (attribute != null && attribute.getType() == AttributeType.ENUM) {
                            customAttributes.put(attribute.getName(), attr.getValue());
                        }
                    }

                    return new ItemDocument(
                            item.getItemId(),
                            item.getName(),
                            item.getDescription(),
                            item.getAddress(),
                            item.getColor() != null ? item.getColor().getName() : null,
                            item.getMaterial() != null ? item.getMaterial().getName() : null,
                            item.getMaker() != null ? item.getMaker().getName() : null,
                            item.getModel() != null ? item.getModel().getName() : null,
                            customAttributes
                    );
                })
                .toList();

        itemSearchRepository.saveAll(documents);
    }
}