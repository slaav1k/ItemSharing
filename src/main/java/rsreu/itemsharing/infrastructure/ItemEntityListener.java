package rsreu.itemsharing.infrastructure;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rsreu.itemsharing.entities.Attribute;
import rsreu.itemsharing.entities.AttributeType;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemAttribute;
import rsreu.itemsharing.repositories.AttributeRepository;
import rsreu.itemsharing.repositories.ItemAttributeRepository;
import rsreu.itemsharing.repositories.ItemSearchRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemEntityListener {

    private static ItemSearchRepository searchRepository;
    private static ItemAttributeRepository itemAttributeRepository;
    private static AttributeRepository attributeRepository;

    @Autowired
    public void init(ItemSearchRepository repo, ItemAttributeRepository itemAttrRepo, AttributeRepository attrRepo) {
        searchRepository = repo;
        itemAttributeRepository = itemAttrRepo;
        attributeRepository = attrRepo;
    }

    @PostPersist
    @PostUpdate
    public void indexItem(Item item) {
        // Получаем дополнительные атрибуты для текущего Item
        List<ItemAttribute> itemAttributes = itemAttributeRepository.findById_Item(item.getItemId());
        Map<String, String> customAttributes = new HashMap<>();
        for (ItemAttribute attr : itemAttributes) {
            Long attributeId = attr.getId().getAttribute();
            Attribute attribute = attributeRepository.findByAttributeId(attributeId);
            if (attribute != null && attribute.getType() == AttributeType.ENUM) {
                customAttributes.put(attribute.getName(), attr.getValue());
            }
        }

        ItemDocument doc = new ItemDocument(
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
        searchRepository.save(doc);
    }
}