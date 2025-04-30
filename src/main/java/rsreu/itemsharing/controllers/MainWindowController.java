package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.infrastructure.ItemDocument;
import rsreu.itemsharing.infrastructure.ItemSearchService;
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

    @GetMapping("/search")
    @ResponseBody
    public List<ItemDocument> search(@RequestParam String q) {
        return itemSearchService.search(q);
    }


    @GetMapping()
    public String catalog(@RequestParam(required = false) Long category,
                          @RequestParam(required = false) Map<String, String> filters,
                          Model model) {
        if (filters != null) {
            filters.remove("category");
            filters.remove("continue");
        }

        List<Item> items;

        if (category != null) {
            Category categoryEntity = categoryRepository.findById(category).orElse(null);
            if (categoryEntity != null) {
                items = itemRepository.findByCategory(categoryEntity);
            } else {
                items = itemRepository.findAll();
            }
        } else {
            items = itemRepository.findAll();
        }

        // Загружаем атрибуты категории
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

        // Применяем фильтры
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
                    Attribute attribute = attributeRepository.findById(itemAttribute.getId().getAttribute()).orElseThrow();
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
        }

        // Загружаем фото товаров
        Map<String, List<String>> photoUrlsMap = new HashMap<>();
        for (Item item : items) {
            List<ItemPhotoLink> itemPhotoLinks = itemPhotoLinkRepository.findByItem(item);
            List<String> photoUrls = itemPhotoLinks.stream()
                    .map(link -> link.getPhotoLink().getUrl())
                    .collect(Collectors.toList());
            // Если у товара нет фотографий, добавляем запасной URL
            if (photoUrls.isEmpty()) {
                photoUrls = Collections.singletonList("default.png"); // Относительный путь к запасному изображению
            }
            photoUrlsMap.put(item.getItemId(), photoUrls);
        }

        // Передаём данные в шаблон
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

        return "catalog";
    }
}