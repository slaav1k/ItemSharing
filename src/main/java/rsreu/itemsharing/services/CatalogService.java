package rsreu.itemsharing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.repositories.*;
import rsreu.itemsharing.security.CustomUserDetails;
import rsreu.itemsharing.infrastructure.ItemDocument;
import rsreu.itemsharing.infrastructure.ItemSearchService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemPhotoLinkRepository itemPhotoLinkRepository;

    @Autowired
    private CategoryAttributeRepository categoryAttributeRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeEnumValueRepository attributeEnumValueRepository;

    @Autowired
    private ItemAttributeRepository itemAttributeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ItemSearchService itemSearchService;

    public Map<String, Object> getCatalogData(Long category, String search, String searchIds, String city,
                                              Map<String, String> filters, CustomUserDetails userDetails) {
        System.out.println("Catalog params - category: " + category + ", search: " + search + ", searchIds: " + searchIds + ", city: " + (city == null ? "null" : "'" + city + "'"));

        // Очистка фильтров
        if (filters != null) {
            filters.remove("category");
            filters.remove("continue");
            filters.remove("search");
            filters.remove("searchIds");
            filters.remove("city");
        }

        // Извлекаем город текущего пользователя
        String userCity = extractUserCity(userDetails);
        System.out.println("User city: " + userCity);

        // Устанавливаем город
        String selectedCity = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        System.out.println("Selected city: " + selectedCity);

        // Получаем список ID из поиска
        List<String> searchItemIds = (searchIds != null && !searchIds.trim().isEmpty())
                ? Arrays.asList(searchIds.split(","))
                : null;
        System.out.println("Parsed search item IDs: " + searchItemIds);

        // 1. Получаем начальный список элементов
        List<Item> items = fetchItems(category, search, searchItemIds);

        // 2. Фильтрация по городу
        if (selectedCity != null && !items.isEmpty()) {
            final String cityFilter = selectedCity;
            items = items.stream()
                    .filter(item -> item.getAddress() != null && item.getAddress().contains(cityFilter))
                    .collect(Collectors.toList());
            System.out.println("Items after city filter: " + items.size());
            System.out.println("Addresses after filter: " + items.stream()
                    .map(Item::getAddress)
                    .collect(Collectors.joining(", ")));
        }

        // 3. Фильтрация по категории, если выбрана (для результатов поиска)
        if (category != null && searchItemIds != null && !searchItemIds.isEmpty()) {
            Category categoryEntity = categoryRepository.findById(category).orElse(null);
            if (categoryEntity != null) {
                items = items.stream()
                        .filter(item -> item.getCategory().getCategoryId().equals(category))
                        .collect(Collectors.toList());
                System.out.println("Items after category filter: " + items.size());
            }
        }

        // 4. Загружаем атрибуты категории
        List<Attribute> attributes = new ArrayList<>();
        Map<Long, List<String>> enumValuesMap = new HashMap<>();

        if (category != null) {
            List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findById_CategoryId(category);
            attributes = categoryAttributes.stream()
                    .map(catAttr -> attributeRepository.findById(catAttr.getId().getAttributeId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            for (Attribute attribute : attributes) {
                if (attribute.getType() == AttributeType.ENUM) {
                    List<String> values = attributeEnumValueRepository.findById_AttributeId(attribute.getAttributeId())
                            .stream()
                            .map(value -> value.getId().getValue())
                            .collect(Collectors.toList());
                    enumValuesMap.put(attribute.getAttributeId(), values);
                }
            }
        }

        List<Color> colors = colorRepository.findAll();
        List<Material> materials = materialRepository.findAll();
        List<Maker> makers = makerRepository.findAll();
        List<rsreu.itemsharing.entities.Model> models = modelRepository.findAll();

        // 5. Применяем фильтры
        items = applyFilters(items, filters);

        // 6. Загружаем фото товаров
        Map<String, List<String>> photoUrlsMap = new HashMap<>();
        for (Item item : items) {
            List<ItemPhotoLink> itemPhotoLinks = itemPhotoLinkRepository.findByItem(item);
            List<String> photoUrls = itemPhotoLinks.stream()
                    .map(link -> normalizePhotoUrl(link.getPhotoLink().getUrl()))
                    .collect(Collectors.toList());
            if (photoUrls.isEmpty()) {
                photoUrls = Collections.singletonList("/images/default.png");
            }
            photoUrlsMap.put(item.getItemId(), photoUrls);
        }

        // 7. Подготавливаем данные для шаблона
        Map<String, Object> modelAttributes = new HashMap<>();
        modelAttributes.put("items", items);
        modelAttributes.put("photoUrlsMap", photoUrlsMap);
        modelAttributes.put("categories", categoryRepository.findAll());
        modelAttributes.put("category", category);
        modelAttributes.put("categoryAttributes", attributes);
        modelAttributes.put("enumValuesMap", enumValuesMap);
        modelAttributes.put("filters", filters);
        modelAttributes.put("colors", colors);
        modelAttributes.put("materials", materials);
        modelAttributes.put("makers", makers);
        modelAttributes.put("models", models);
        modelAttributes.put("search", search);
        modelAttributes.put("searchIds", searchIds);
        modelAttributes.put("city", selectedCity);
        modelAttributes.put("userCity", userCity);

        return modelAttributes;
    }

    private String extractUserCity(CustomUserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        User user = userDetails.getUser();
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            String[] addressParts = user.getAddress().split(", ");
            if (addressParts.length > 0) {
                return addressParts[0].replace("г. ", "").trim();
            }
        }
        return null;
    }

    private List<Item> fetchItems(Long category, String search, List<String> searchItemIds) {
        List<Item> items;
        if (searchItemIds != null && !searchItemIds.isEmpty()) {
//            items = itemRepository.findAllById(searchItemIds);
            items = itemRepository.findAllByItemIdInAndIsBlockedFalse(searchItemIds);
            System.out.println("Items fetched by search IDs: " + items.size());
        } else if (search != null && !search.trim().isEmpty()) {
            items = Collections.emptyList();
            System.out.println("No search results for query: " + search);
        } else {
            if (category != null) {
                Category categoryEntity = categoryRepository.findById(category).orElse(null);
                if (categoryEntity != null) {
//                    items = itemRepository.findByCategory(categoryEntity);
                    items = itemRepository.findByCategoryAndIsBlockedFalse(categoryEntity);
                    System.out.println("Items fetched by category: " + items.size());
                } else {
                    items = itemRepository.findAllByIsBlockedFalse();
//                    items = itemRepository.findAll();
                    System.out.println("All items fetched (no category): " + items.size());
                }
            } else {
                items = itemRepository.findAllByIsBlockedFalse();
//                items = itemRepository.findAll();
                System.out.println("All items fetched (no search, no category): " + items.size());
            }
        }
        return items;
    }

    private List<Item> applyFilters(List<Item> items, Map<String, String> filters) {
        if (filters == null || filters.isEmpty()) {
            return items;
        }

        boolean allFiltersNull = true;
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            String filterValue = filter.getValue();
            if (!Objects.equals(filterValue, "")) {
                allFiltersNull = false;
                break;
            }
        }
        if (allFiltersNull) {
            return items;
        }

        List<Item> filteredItems = new ArrayList<>();
        for (Item item : items) {
            List<ItemAttribute> itemAttributes = itemAttributeRepository.findById_Item(item.getItemId());
            Map<String, String> itemAttributeMap = new HashMap<>();

            itemAttributeMap.put("color", String.valueOf(item.getColor().getColorId()));
            itemAttributeMap.put("material", String.valueOf(item.getMaterial().getMaterialId()));
            itemAttributeMap.put("maker", String.valueOf(item.getMaker().getMakerId()));
            itemAttributeMap.put("model", String.valueOf(item.getModel().getModelId()));

            for (ItemAttribute itemAttribute : itemAttributes) {
                Attribute attribute = attributeRepository.findById(itemAttribute.getId().getAttribute()).orElse(null);
                if (attribute != null) {
                    String attributeName = attribute.getName();
                    AttributeType type = attribute.getType();
                    if (type == AttributeType.ENUM) {
                        itemAttributeMap.put(attributeName, itemAttribute.getValue());
                    } else if (type == AttributeType.NUMBER) {
                        try {
                            double value = Double.parseDouble(itemAttribute.getValue());
                            itemAttributeMap.put(attributeName, Double.toString(value));
                        } catch (NumberFormatException e) {
                            itemAttributeMap.put(attributeName, "Ошибка данных");
                        }
                    }
                }
            }

            boolean matches = true;
            for (Map.Entry<String, String> filter : filters.entrySet()) {
                String filterKey = filter.getKey();
                String filterValue = filter.getValue();

                if (filterValue == null || filterValue.trim().isEmpty()) {
                    continue;
                }

                if (filterKey.endsWith("_min") || filterKey.endsWith("_max")) {
                    String baseKey = filterKey.replace("_min", "").replace("_max", "");
                    if (itemAttributeMap.containsKey(baseKey)) {
                        double itemValue = Double.parseDouble(itemAttributeMap.get(baseKey));
                        if (filterKey.endsWith("_min") && itemValue < Double.parseDouble(filterValue)) {
                            matches = false;
                            break;
                        }
                        if (filterKey.endsWith("_max") && itemValue > Double.parseDouble(filterValue)) {
                            matches = false;
                            break;
                        }
                    }
                } else {
                    if (itemAttributeMap.containsKey(filterKey)) {
                        if (!itemAttributeMap.get(filterKey).equals(filterValue)) {
                            matches = false;
                            break;
                        }
                    }
                }
            }

            if (matches) {
                filteredItems.add(item);
            }
        }

        System.out.println("Items after attribute filters: " + filteredItems.size());
        return filteredItems;
    }

    private String normalizePhotoUrl(String url) {
        if (url.startsWith("http")) {
            return url;
        } else if (url.startsWith("items/")) {
            return "/images/" + url;
        }
        return "/images/default.png";
    }
}