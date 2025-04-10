package rsreu.itemsharing.controllers;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.repositories.*;
import rsreu.itemsharing.security.CustomUserDetails;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemPhotoLinkRepository itemPhotoLinkRepository;

    @Autowired
    private ItemReviewPhotoLinkRepository itemReviewPhotoLinkRepository;

    @Autowired
    private ItemReviewRepository itemReviewRepository;

    @Autowired
    private ItemAttributeRepository itemAttributeRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private PhotoLinkRepository photoLinkRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestStatusRepository requestStatusRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryAttributeRepository categoryAttributeRepository;

    @Autowired
    private AttributeEnumValueRepository attributeEnumValueRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ServletContext servletContext;

    @GetMapping("/item/{itemId}")
    public String getItem(@PathVariable("itemId") String itemId, Model model) {
        Item item = itemRepository.findById(itemId).get();
        model.addAttribute("item", item);

        Map<String, List<String>> photoUrlsMap = new HashMap<>();

        List<ItemPhotoLink> itemPhotoLinks = itemPhotoLinkRepository.findByItem(item);
        List<String> photoUrls = new ArrayList<>();
        for (ItemPhotoLink itemPhotoLink : itemPhotoLinks) {
            photoUrls.add(itemPhotoLink.getPhotoLink().getUrl());
        }
        photoUrlsMap.put(item.getItemId(), photoUrls);

        model.addAttribute("photoUrlsMap", photoUrlsMap);

        List<ItemReview> reviews = itemReviewRepository.findByItem(item);
        Map<Long, List<String>> reviewPhotosMap = new HashMap<>();
        for (ItemReview review : reviews) {
            List<ItemReviewPhotoLink> reviewPhotos = itemReviewPhotoLinkRepository.findByItemReview(review);
            List<String> reviewPhotoUrls = new ArrayList<>();
            for (ItemReviewPhotoLink photoLink : reviewPhotos) {
                reviewPhotoUrls.add(photoLink.getPhotoLink().getUrl());
            }
            reviewPhotosMap.put(review.getItemReviewId(), reviewPhotoUrls);
        }
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewPhotosMap", reviewPhotosMap);

        String randomColor = generateRandomColor();
        model.addAttribute("randomColor", randomColor);

        // Загружаем дополнительные атрибуты
        List<ItemAttribute> itemAttributes = itemAttributeRepository.findById_Item(item.getItemId());
        Map<String, String> attributeMap = new HashMap<>();
        for (ItemAttribute itemAttribute : itemAttributes) {
            Attribute attribute = attributeRepository.findById(itemAttribute.getId().getAttribute()).orElseThrow();
            AttributeType type = attribute.getType();
            if (type == AttributeType.ENUM) {
                attributeMap.put(attribute.getName(), itemAttribute.getValue());
            } else {
                attributeMap.put(attribute.getName(), itemAttribute.getValue());
            }

        }
        model.addAttribute("attributes", attributeMap);

        // Получаем все запросы на бронирование для этого товара
        List<Request> requests = requestRepository.findByItem(item);

        // Список для хранения забронированных дат
        List<String> bookedDates = new ArrayList<>();

        // Обрабатываем каждый запрос на бронирование
        for (Request request : requests) {
            if (request.getStatus().getStatusId() >= 2 && request.getStatus().getStatusId() <= 4) {
                LocalDate startDate = request.getStartDate();
                LocalDate endDate = request.getEndDate();

                // Добавляем все даты из диапазона в список забронированных дат
                while (!startDate.isAfter(endDate)) {
                    // Форматируем дату в строку и добавляем в список
                    bookedDates.add(startDate.toString());
                    startDate = startDate.plusDays(1);
                }
            }

        }
        System.out.println(bookedDates);
        model.addAttribute("bookedDates", bookedDates);
        model.addAttribute("owner", item.getOwner());
        return "itemCard";
    }

    @PostMapping("/item/{itemId}/review")
    public String addReview(@PathVariable("itemId") String itemId,
                            @RequestParam("comment") String comment,
                            @RequestParam("score") int score,
                            @RequestParam("photo") MultipartFile[] photos,
                            Model model) {

        // Получаем текущего пользователя
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();
        String username = currentUser.getFullName();

        // Создаем новый отзыв
        Item item = itemRepository.findById(itemId).orElseThrow();
        ItemReview review = new ItemReview();
        review.setItem(item);
        review.setReviewer(currentUser);
        review.setComment(comment);
        review.setScore(score);
        review.setDate(LocalDate.now());

        itemReviewRepository.save(review);

        if (photos != null && photos.length > 0) {
            // Путь к папке для сохранения изображений, с использованием относительного пути
            String uploadDir = "C:\\Java Projects\\ItemSharing\\src\\main\\resources\\static\\images\\items";

            // Создаем папку, если она не существует
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean dirCreated = directory.mkdirs();
                if (dirCreated) {
                    System.out.println("Папка была успешно создана.");
                } else {
                    System.out.println("Не удалось создать папку.");
                    return "error"; // Возвращаем ошибку, если папку не удалось создать
                }
            }

            for (MultipartFile photo : photos) {
                // Формируем путь для сохранения файла
                Path filePath = Paths.get(uploadDir, photo.getOriginalFilename());
                System.out.println("Файл будет сохранен по пути: " + filePath.toString()); // Логируем путь к файлу

                // Сохраняем файл на диск
                try {
                    photo.transferTo(filePath.toFile());
                    System.out.println("Файл " + photo.getOriginalFilename() + " успешно сохранен.");
                } catch (IOException e) {
                    e.printStackTrace(); // Логируем ошибку
                    return "error";
                }

                // Создаем ссылку на фото и сохраняем в базе данных
                PhotoLink photoLink = new PhotoLink();
                photoLink.setUrl("items/" + photo.getOriginalFilename()); // Сохраняем путь к файлу
                photoLinkRepository.save(photoLink);

                ItemReviewPhotoLink reviewPhotoLink = new ItemReviewPhotoLink();
                ItemReviewPhotoLinkId id = new ItemReviewPhotoLinkId(review.getItemReviewId(), photoLink.getPhotoId());
                reviewPhotoLink.setId(id);
                reviewPhotoLink.setItemReview(review);
                reviewPhotoLink.setPhotoLink(photoLink);
                itemReviewPhotoLinkRepository.save(reviewPhotoLink);
            }
        }


        return "redirect:/item/" + itemId; // Перенаправление на страницу товара
    }

    @GetMapping("/item/{itemId}/request")
    public String getItemRequest(@PathVariable("itemId") String itemId, Model model) {
        // Получаем товар
        Item item = itemRepository.findById(itemId).orElseThrow();

        // Получаем все забронированные даты для данного товара
        List<Request> requests = requestRepository.findByItem(item);
        List<LocalDate> bookedDates = new ArrayList<>();
        for (Request request : requests) {
            LocalDate startDate = request.getStartDate();
            LocalDate endDate = request.getEndDate();
            // Добавляем все даты из диапазона в список забронированных дат
            while (!startDate.isAfter(endDate)) {
                bookedDates.add(startDate);
                startDate = startDate.plusDays(1);
            }
        }

        // Добавляем все забронированные даты в модель
        model.addAttribute("bookedDates", bookedDates);
        model.addAttribute("item", item);

        return "itemRequest";
    }

    @PostMapping("/item/{itemId}/request")
    public String createRequest(@PathVariable("itemId") String itemId,
                                @RequestParam("startDate") LocalDate startDate,
                                @RequestParam("endDate") LocalDate endDate,
                                Model model) {

        // Получаем товар
        Item item = itemRepository.findById(itemId).orElseThrow();

        // Получаем текущего пользователя
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        // Проверка, не пересекаются ли выбранные даты с уже забронированными
        List<Request> requests = requestRepository.findByItem(item);
        List<LocalDate> bookedDates = new ArrayList<>();
        for (Request request : requests) {
            LocalDate startDateFor = request.getStartDate();
            LocalDate endDateFor = request.getEndDate();
            // Добавляем все даты из диапазона в список забронированных дат
            while (!startDateFor.isAfter(endDateFor)) {
                bookedDates.add(startDateFor);
                startDateFor = startDateFor.plusDays(1);
            }
        }

        boolean datesOverlap = false;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (bookedDates.contains(date)) {
                datesOverlap = true;
                break;
            }
        }

        if (datesOverlap) {
            model.addAttribute("error", "Вы выбрали даты, которые уже забронированы.");
            return "itemRequest"; // Возвращаем пользователя на страницу с ошибкой
        }

        // Создаем и сохраняем новую заявку
        RequestStatus status = requestStatusRepository.findById(1L).orElseThrow();
        Request request = new Request();
        request.setHolder(currentUser);
        request.setItem(item);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setStatus(status);

        requestRepository.save(request);

        return "redirect:/item/" + itemId; // Перенаправляем на страницу товара
    }

    @GetMapping("/selectCategory")
    public String selectCategory(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "selectCategory";
    }

    @GetMapping("/createItem")
    public String createItemForm(@RequestParam(name = "category", required = true) Long category, Model model) {
        // Получаем категорию по ID
        Category categoryEntity = categoryRepository.findById(category).orElseThrow();

        // Создаем новый объект Item для заполнения
        Item newItem = new Item();
        newItem.setCategory(categoryEntity); // Присваиваем выбранную категорию

        // Получаем все атрибуты для выбранной категории
        List<Attribute> attributes = new ArrayList<>();
        Map<Long, List<String>> enumValuesMap = new HashMap<>();
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

        // Загружаем все значения для выпадающих списков
        List<Color> colors = colorRepository.findAll();
        List<Material> materials = materialRepository.findAll();
        List<Maker> makers = makerRepository.findAll();
        List<rsreu.itemsharing.entities.Model> models = modelRepository.findAll();


        // Добавляем атрибуты в модель, чтобы можно было их заполнить
        model.addAttribute("newItem", newItem);
        model.addAttribute("categoryAttributes", attributes);
        model.addAttribute("enumValuesMap", enumValuesMap);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("category", categoryEntity); // Для отображения информации о категории
        model.addAttribute("colors", colors);
        model.addAttribute("materials", materials);
        model.addAttribute("makers", makers);
        model.addAttribute("models", models);
        // Возвращаем страницу с формой для создания товара
        return "createItem";
    }

    @PostMapping("/saveItem")
    public String saveItem(@ModelAttribute Item newItem,
                           @ModelAttribute Category categoryEntity,
                           @RequestParam Long categoryId,
                           @RequestParam Map<String, String> attributes,
                           @RequestParam("photos") MultipartFile[] photos,
                           @RequestParam("colorId") Long colorId,
                           @RequestParam("materialId") Long materialId,
                           @RequestParam("makerId") Long makerId,
                           @RequestParam("modelId") Long modelId,
                           Model model) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();
        // Сохраняем товар
        newItem.setAvailable(true);
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        newItem.setCategory(category);
        newItem.setOwner(currentUser);

        // Устанавливаем сущности по их ID
        newItem.setColor(colorRepository.findById(colorId).orElseThrow());
        newItem.setMaterial(materialRepository.findById(materialId).orElseThrow());
        newItem.setMaker(makerRepository.findById(makerId).orElseThrow());
        newItem.setModel(modelRepository.findById(modelId).orElseThrow());

        itemRepository.save(newItem);

        // Сохраняем атрибуты
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String value = entry.getValue();

            // Пропускаем атрибуты с именами из латинских букв
            if (attributeName.matches("^[a-zA-Z]+$")) {
                continue;
            }

            // Остальная логика обработки атрибута
            Attribute attribute = attributeRepository.findByName(attributeName);
            if (attribute == null) {
                continue;
            }


            // Создаем новый ItemAttribute
            ItemAttributeId itemAttributeId = new ItemAttributeId(newItem.getItemId(), attribute.getAttributeId());
            ItemAttribute itemAttribute = new ItemAttribute();
            itemAttribute.setId(itemAttributeId);
            itemAttribute.setValue(value);

            itemAttributeRepository.save(itemAttribute);
        }

        // Обрабатываем загрузку фото
        if (photos != null && photos.length > 0) {
            String uploadDir = "C:\\Java Projects\\ItemSharing\\src\\main\\resources\\static\\images\\items";

            // Создаем папку, если она не существует
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean dirCreated = directory.mkdirs();
                if (!dirCreated) {
                    return "error";
                }
            }

            for (MultipartFile photo : photos) {
                Path filePath = Paths.get(uploadDir, photo.getOriginalFilename());

                try {
                    photo.transferTo(filePath.toFile());

                    // Создаем ссылку на фото
                    PhotoLink photoLink = new PhotoLink();
                    photoLink.setUrl("items/" + photo.getOriginalFilename());
                    photoLinkRepository.save(photoLink);

                    // Сохраняем связь с товаром

                    ItemPhotoLinkId itemPhotoLinkId = new ItemPhotoLinkId(newItem.getItemId(), photoLink.getPhotoId());
                    ItemPhotoLink itemPhotoLink = new ItemPhotoLink(itemPhotoLinkId, newItem, photoLink);
                    itemPhotoLinkRepository.save(itemPhotoLink);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "error";
                }
            }
        }

        // Перенаправляем на страницу товара
        return "redirect:/item/" + newItem.getItemId();
    }

    @GetMapping("/editItem/{itemId}")
    public String editItemForm(@PathVariable String itemId, Model model) {
        Item item = itemRepository.findById(itemId).orElseThrow();

        List<Attribute> attributes = new ArrayList<>();
        Map<Long, List<String>> enumValuesMap = new HashMap<>();
        List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findById_CategoryId(item.getCategory().getCategoryId());

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

        // Загружаем дополнительные атрибуты
        List<ItemAttribute> itemAttributes = itemAttributeRepository.findById_Item(item.getItemId());
        Map<String, String> attributeMap = new HashMap<>();
        for (ItemAttribute itemAttribute : itemAttributes) {
            Attribute attribute = attributeRepository.findById(itemAttribute.getId().getAttribute()).orElseThrow();
            AttributeType type = attribute.getType();
            if (type == AttributeType.ENUM) {
                attributeMap.put(attribute.getName(), itemAttribute.getValue());
            } else {
                attributeMap.put(attribute.getName(), itemAttribute.getValue());
            }

        }

        // Загружаем все значения для выпадающих списков
        List<Color> colors = colorRepository.findAll();
        List<Material> materials = materialRepository.findAll();
        List<Maker> makers = makerRepository.findAll();
        List<rsreu.itemsharing.entities.Model> models = modelRepository.findAll();



        model.addAttribute("attributes", attributeMap);
        model.addAttribute("item", item);
        model.addAttribute("categoryAttributes", attributes);
        model.addAttribute("enumValuesMap", enumValuesMap);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("category", item.getCategory());
        model.addAttribute("colors", colors);
        model.addAttribute("materials", materials);
        model.addAttribute("makers", makers);
        model.addAttribute("models", models);
        model.addAttribute("selectedMakerId", item.getMaker().getMakerId());
        return "editItem";
    }


    @PostMapping("/updateItem")
    public String updateItem(@ModelAttribute Item updatedItem,
                             @RequestParam Map<String, String> attributes,
                             @RequestParam("photos") MultipartFile[] photos,
                             @RequestParam("colorId") Long colorId,
                             @RequestParam("materialId") Long materialId,
                             @RequestParam("makerId") Long makerId,
                             @RequestParam("modelId") Long modelId) {

        Item item = itemRepository.findById(updatedItem.getItemId()).orElseThrow();
        item.setName(updatedItem.getName());
        item.setDescription(updatedItem.getDescription());
        item.setAddress(updatedItem.getAddress());
        item.setWeight(updatedItem.getWeight());

        item.setColor(colorRepository.findById(colorId).orElseThrow());
        item.setMaterial(materialRepository.findById(materialId).orElseThrow());
        item.setMaker(makerRepository.findById(makerId).orElseThrow());
        item.setModel(modelRepository.findById(modelId).orElseThrow());
        item.setReleaseYear(updatedItem.getReleaseYear());

        itemRepository.save(item);

        // Обновляем атрибуты
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String value = entry.getValue();

            Attribute attribute = attributeRepository.findByName(attributeName);
            if (attribute == null) {
                continue;
            }

            ItemAttributeId itemAttributeId = new ItemAttributeId(item.getItemId(), attribute.getAttributeId());
            ItemAttribute itemAttribute = itemAttributeRepository.findById(itemAttributeId)
                    .orElse(new ItemAttribute(itemAttributeId, value));

            itemAttribute.setValue(value);
            itemAttributeRepository.save(itemAttribute);
        }

        // Обновляем фото
        if (photos != null && photos.length > 0) {
            // Логика обновления фото
        }

        return "redirect:/item/" + item.getItemId();
    }

    @PostMapping("/deleteItem/{itemId}")
    @Transactional
    public String deleteItem(@PathVariable String itemId) {
        // Удаляем связанные атрибуты
        itemAttributeRepository.deleteById_Item(itemId);

        // Удаляем связанные фотографии
        itemPhotoLinkRepository.deleteByItem_ItemId(itemId);

        // Удаляем сам товар
        itemRepository.deleteById(itemId);

        return "redirect:/";
    }







    private String generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
