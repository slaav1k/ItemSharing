package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.security.CustomUserDetails;
import rsreu.itemsharing.infrastructure.ItemDocument;
import rsreu.itemsharing.infrastructure.ItemSearchService;
import rsreu.itemsharing.services.CityService;
import rsreu.itemsharing.repositories.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller("/")
public class MainWindowController {

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

    @Autowired
    private CityService cityService;

    @GetMapping("/search")
    @ResponseBody
    public List<ItemDocument> search(@RequestParam String q, @RequestParam(required = false) String city) {
        System.out.println("Search query received: q=" + q + ", city=" + (city == null ? "null" : "'" + city + "'"));
        List<ItemDocument> results = itemSearchService.search(q, city);
        System.out.println("Search results: " + results.size() + " items");
        return results;
    }

    @GetMapping
    public String catalog(@RequestParam(required = false) Long category,
                          @RequestParam(required = false) String search,
                          @RequestParam(required = false) String searchIds,
                          @RequestParam(required = false) String city,
                          @RequestParam(required = false) Map<String, String> filters,
                          Model model) {
        System.out.println("Catalog params - category: " + category + ", search: " + search + ", searchIds: " + searchIds + ", city: " + (city == null ? "null" : "'" + city + "'"));

        if (filters != null) {
            filters.remove("category");
            filters.remove("continue");
            filters.remove("search");
            filters.remove("searchIds");
            filters.remove("city");
        }

        // Извлекаем город текущего пользователя
        String userCity = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            User user = userDetails.getUser();
            if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                String[] addressParts = user.getAddress().split(", ");
                if (addressParts.length > 0) {
                    userCity = addressParts[0].replace("г. ", "").trim();
                }
            }
        }
        System.out.println("User city: " + userCity);

        // Устанавливаем город: используем city из параметров, если он не пустой, иначе null
        String selectedCity = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        System.out.println("Selected city: " + selectedCity);

        // Получаем список ID из поиска
        List<String> searchItemIds = (searchIds != null && !searchIds.trim().isEmpty())
                ? Arrays.asList(searchIds.split(","))
                : null;
        System.out.println("Parsed search item IDs: " + searchItemIds);

        List<Item> items = new ArrayList<>();

        // 1. Получаем начальный список элементов
        if (searchItemIds != null && !searchItemIds.isEmpty()) {
            items = itemRepository.findAllById(searchItemIds);
            System.out.println("Items fetched by search IDs: " + items.size());
        } else if (search != null && !search.trim().isEmpty()) {
            items = Collections.emptyList();
            System.out.println("No search results for query: " + search);
        } else {
            if (category != null) {
                Category categoryEntity = categoryRepository.findById(category).orElse(null);
                if (categoryEntity != null) {
                    items = itemRepository.findByCategory(categoryEntity);
                    System.out.println("Items fetched by category: " + items.size());
                } else {
                    items = itemRepository.findAll();
                    System.out.println("All items fetched (no category): " + items.size());
                }
            } else {
                items = itemRepository.findAll();
                System.out.println("All items fetched (no search, no category): " + items.size());
            }
        }

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

        // 3. Фильтрация по категории, если выбрана
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
        boolean allFiltersNull = true;
        if (filters != null) {
            for (Map.Entry<String, String> filter : filters.entrySet()) {
                String filterValue = filter.getValue();
                if (!Objects.equals(filterValue, "")) {
                    allFiltersNull = false;
                    break;
                }
            }
        }

        if (filters != null && !filters.isEmpty() && !allFiltersNull) {
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

            items = filteredItems;
            System.out.println("Items after attribute filters: " + items.size());
        }

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

        // 7. Передаём данные в шаблон
        model.addAttribute("items", items);
        model.addAttribute("photoUrlsMap", photoUrlsMap);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("category", category);
        model.addAttribute("categoryAttributes", attributes);
        model.addAttribute("enumValuesMap", enumValuesMap);
        model.addAttribute("filters", filters);
        model.addAttribute("colors", colors);
        model.addAttribute("materials", materials);
        model.addAttribute("makers", makers);
        model.addAttribute("models", models);
        model.addAttribute("search", search);
        model.addAttribute("searchIds", searchIds);
        model.addAttribute("cities", cityService.getCities());
        model.addAttribute("city", selectedCity);
        model.addAttribute("userCity", userCity);

        return "catalog";
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