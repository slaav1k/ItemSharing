package rsreu.itemsharing.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.repositories.AttributeRepository;
import rsreu.itemsharing.repositories.ItemAttributeRepository;
import rsreu.itemsharing.repositories.ItemSearchRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemIndexingService {

    @Autowired
    private ItemAttributeRepository itemAttributeRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private ItemSearchRepository searchRepository;

    @Async
    public void indexItemAsync(Item item) {
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