package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.repositories.*;

import java.sql.*;
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

//    @GetMapping()
//    public String catalog(@RequestParam(required = false) Long category,
//            @RequestParam(required = false) Map<String, String> filters, Model model) {
//        List<Item> items;
//
//        if (category != null) {
//            Category categoryEntity = categoryRepository.findById(category).orElse(null);
//            if (categoryEntity != null) {
//                items = itemRepository.findByCategory(categoryEntity);
//            } else {
//                items = itemRepository.findAll();
//            }
//        } else {
//            items = itemRepository.findAll();
//        }
//
//        Map<String, List<String>> photoUrlsMap = new HashMap<>();
//
//
//        for (Item item : items) {
//            List<ItemPhotoLink> itemPhotoLinks = itemPhotoLinkRepository.findByItem(item);
//            List<String> photoUrls = new ArrayList<>();
//            for (ItemPhotoLink itemPhotoLink : itemPhotoLinks) {
//                photoUrls.add(itemPhotoLink.getPhotoLink().getUrl());
//            }
//            photoUrlsMap.put(item.getItemId(), photoUrls);
//        }
//
//
//        model.addAttribute("items", items);
//        model.addAttribute("photoUrlsMap", photoUrlsMap);
//
//        if (category != null) {
//            List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findById_CategoryId(category);
//            model.addAttribute("category", category);
//            model.addAttribute("categoryAttributes", categoryAttributes);
//        }
//
//
//        return "catalog";
//    }

//    @GetMapping()
//    public String catalog(@RequestParam(required = false) Long category, Model model) {
//        List<Item> items;
//
//        if (category != null) {
//            Category categoryEntity = categoryRepository.findById(category).orElse(null);
//            if (categoryEntity != null) {
//                items = itemRepository.findByCategory(categoryEntity);
//            } else {
//                items = itemRepository.findAll();
//            }
//        } else {
//            items = itemRepository.findAll();
//        }
//
//        Map<String, List<String>> photoUrlsMap = new HashMap<>();
//
//
//        for (Item item : items) {
//            List<ItemPhotoLink> itemPhotoLinks = itemPhotoLinkRepository.findByItem(item);
//            List<String> photoUrls = new ArrayList<>();
//            for (ItemPhotoLink itemPhotoLink : itemPhotoLinks) {
//                photoUrls.add(itemPhotoLink.getPhotoLink().getUrl());
//            }
//            photoUrlsMap.put(item.getItemId(), photoUrls);
//        }
//
//
//        model.addAttribute("items", items);
//        model.addAttribute("photoUrlsMap", photoUrlsMap);
//        model.addAttribute("categories", categoryRepository.findAll());
//        if (category != null) {
//            List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findById_CategoryId(category);
//
//            List<Attribute> attributes = categoryAttributes.stream()
//                    .map(categoryAttribute -> attributeRepository.findByAttributeId(categoryAttribute.getId().getAttributeId()))
//                    .collect(Collectors.toList());
//
//            Map<Long, List<String>> enumValuesMap = new HashMap<>();
//            for (Attribute attribute : attributes) {
//                if (attribute.getType() == AttributeType.ENUM) {
//                    List<String> values = attributeEnumValueRepository.findById_AttributeId(attribute.getAttributeId())
//                            .stream()
//                            .map(value -> value.getId().getValue()) // Получаем сами строки значений
//                            .collect(Collectors.toList());
//                    enumValuesMap.put(attribute.getAttributeId(), values);
//                }
//            }
//
//            // Добавляем атрибуты в модель
//            model.addAttribute("category", category);
//            model.addAttribute("categoryAttributes", attributes);
//            model.addAttribute("enumValuesMap", enumValuesMap);
//        }
//
//
//        return "catalog";
//    }

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

            // Загружаем возможные значения ENUM
            for (Attribute attribute : attributes) {
                if (attribute.getType() == AttributeType.ENUM) {
                    List<String> values = attributeEnumValueRepository.findById_AttributeId(attribute.getAttributeId())
                            .stream()
                            .map(value -> value.getId().getValue()) // Достаём значения
                            .collect(Collectors.toList());
                    enumValuesMap.put(attribute.getAttributeId(), values);
                }
            }
        }

        boolean allFiltersNull  = true;
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            String filterKey = filter.getKey();
            String filterValue = filter.getValue();
            if (!Objects.equals(filterValue, "")) {
                allFiltersNull = false;
                break;
            }
        }


        // **Применяем фильтры** (если они заданы)
        if (filters != null && !filters.isEmpty() && !allFiltersNull) {
            List<Item> filteredItems = new ArrayList<>();

            for (Item item : items) {
                List<ItemAttribute> itemAttributes = itemAttributeRepository.findById_Item(item.getItemId());
                Map<String, String> itemAttributeMap = new HashMap<>();

                if (itemAttributes.isEmpty()) {
                    continue;
                }

                // Заполняем карту атрибутов товара
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
//                        itemAttributeMap.put(attributeName, itemAttribute.getValueNumber().toString());
                    }
                }

                // Проверяем, проходит ли товар фильтры
                boolean matches = true;

                for (Map.Entry<String, String> filter : filters.entrySet()) {
                    String filterKey = filter.getKey();
                    String filterValue = filter.getValue();

                    if (filterValue == null || filterValue.trim().isEmpty()) {
                        continue;
                    }

                    // Числовые фильтры (мин/макс)
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
                    } else { // ENUM
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
            List<String> photoUrls = itemPhotoLinks.stream().map(link -> link.getPhotoLink().getUrl()).toList();
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

        return "catalog";
    }


}
