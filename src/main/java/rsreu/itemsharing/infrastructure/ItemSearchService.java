package rsreu.itemsharing.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.Attribute;
import rsreu.itemsharing.entities.AttributeType;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemAttribute;
import rsreu.itemsharing.repositories.AttributeRepository;
import rsreu.itemsharing.repositories.ItemAttributeRepository;
import rsreu.itemsharing.repositories.ItemSearchRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ItemSearchService.class);
    private final ItemSearchRepository repository;
    private final ItemAttributeRepository itemAttributeRepository;
    private final AttributeRepository attributeRepository;

    @Autowired
    public ItemSearchService(ItemSearchRepository repository, ItemAttributeRepository itemAttributeRepository,
                             AttributeRepository attributeRepository) {
        this.repository = repository;
        this.itemAttributeRepository = itemAttributeRepository;
        this.attributeRepository = attributeRepository;
    }

    public void index(Item item) {
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
        repository.save(doc);
    }

    public List<ItemDocument> search(String query, String city) {
        if (query == null || query.trim().isEmpty()) {
            logger.warn("Empty or null search query provided, returning empty list");
            return Collections.emptyList();
        }

        logger.info("Searching items with query: {}, city: {}", query, city);

        // Нормализация города
        String normalizedCity = city != null ? city.trim().toLowerCase() : null;

        // Проверка элементов по городу (для отладки)
        if (normalizedCity != null && !normalizedCity.isEmpty()) {
            List<ItemDocument> cityItems = repository.findByCity(normalizedCity);
            logger.info("Found {} items in city: {}", cityItems.size(), normalizedCity);
        }

        // Если город не указан, выполняем обычный поиск
        if (normalizedCity == null || normalizedCity.isEmpty()) {
            List<ItemDocument> results = repository.searchByMultipleFields(query);
            logger.info("Found {} items without city filter", results.size());
            return results;
        }

        // Выполняем поиск с фильтром по городу
        List<ItemDocument> results = repository.searchByQueryAndCity(query, normalizedCity);
        logger.info("Found {} items with query: {} and city: {}", results.size(), query, normalizedCity);
        return results;
    }
}