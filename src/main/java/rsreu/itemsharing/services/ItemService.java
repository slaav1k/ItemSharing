package rsreu.itemsharing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.infrastructure.ItemIndexingService;
import rsreu.itemsharing.repositories.*;
import rsreu.itemsharing.security.CustomUserDetails;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemService {

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
    private S3Service s3Service;

    @Autowired
    private ItemIndexingService itemIndexingService;

    @Autowired
    private CityService cityService;

    public Map<String, Object> getEditItemFormData(String itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        Map<String, Object> modelAttributes = new HashMap<>();

        // Разделение адреса на город, улицу и дом
        String city = "";
        String street = "";
        String house = "";
        if (item.getAddress() != null && !item.getAddress().isEmpty()) {
            String[] addressParts = item.getAddress().split(",\\s*");
            if (addressParts.length >= 3) {
                city = addressParts[0].trim();
                street = addressParts[1].trim();
                house = addressParts[2].trim();
            } else if (addressParts.length == 2) {
                city = addressParts[0].trim();
                street = addressParts[1].trim();
            } else if (addressParts.length == 1) {
                city = addressParts[0].trim();
            }
        }

        // Добавление списка городов
        modelAttributes.put("cities", cityService.getCities());
        modelAttributes.put("itemCity", city);
        modelAttributes.put("itemStreet", street);
        modelAttributes.put("itemHouse", house);

        List<Attribute> attributes = categoryAttributeRepository.findById_CategoryId(item.getCategory().getCategoryId()).stream()
                .map(catAttr -> attributeRepository.findById(catAttr.getId().getAttributeId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Long, List<String>> enumValuesMap = new HashMap<>();
        for (Attribute attribute : attributes) {
            if (attribute.getType() == AttributeType.ENUM) {
                List<String> values = attributeEnumValueRepository.findById_AttributeId(attribute.getAttributeId())
                        .stream()
                        .map(value -> value.getId().getValue())
                        .collect(Collectors.toList());
                enumValuesMap.put(attribute.getAttributeId(), values);
            }
        }

        List<ItemAttribute> itemAttributes = itemAttributeRepository.findById_Item(item.getItemId());
        Map<String, String> attributeMap = new HashMap<>();
        for (ItemAttribute itemAttribute : itemAttributes) {
            Attribute attribute = attributeRepository.findById(itemAttribute.getId().getAttribute()).orElseThrow();
            attributeMap.put(attribute.getName(), itemAttribute.getValue());
        }

        List<Map<String, Object>> photoData = itemPhotoLinkRepository.findByItem(item)
                .stream()
                .map(link -> {
                    Map<String, Object> photoInfo = new HashMap<>();
                    photoInfo.put("photoId", link.getId().getPhotoId());
                    photoInfo.put("url", normalizePhotoUrl(link.getPhotoLink().getUrl()));
                    return photoInfo;
                })
                .collect(Collectors.toList());

        modelAttributes.put("attributes", attributeMap);
        modelAttributes.put("item", item);
        modelAttributes.put("categoryAttributes", attributes);
        modelAttributes.put("enumValuesMap", enumValuesMap);
        modelAttributes.put("categories", categoryRepository.findAll());
        modelAttributes.put("category", item.getCategory());
        modelAttributes.put("colors", colorRepository.findAll());
        modelAttributes.put("materials", materialRepository.findAll());
        modelAttributes.put("makers", makerRepository.findAll());
        modelAttributes.put("models", modelRepository.findAll());
        modelAttributes.put("selectedMakerId", item.getMaker().getMakerId());
        modelAttributes.put("photos", photoData);

        return modelAttributes;
    }

    @Transactional
    public String updateItem(Item updatedItem, Map<String, String> attributes, MultipartFile[] photos,
                             Long colorId, Long materialId, Long makerId, Long modelId,
                             List<Long> photosToDelete, String city, String street, String house) {
        Item item = itemRepository.findById(updatedItem.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Вещь с ID " + updatedItem.getItemId() + " не найдена"));

        // Обновление основных полей вещи
        item.setName(updatedItem.getName());
        item.setDescription(updatedItem.getDescription());
        item.setAddress(String.format("%s, %s, %s", city, street, house));
        item.setWeight(updatedItem.getWeight());
        item.setReleaseYear(updatedItem.getReleaseYear());
        item.setBlocked(false);

        // Валидация и обновление связанных сущностей
        if (colorId == null || colorRepository.findById(colorId).isEmpty()) {
            throw new IllegalArgumentException("Неверный ID цвета: " + colorId);
        }
        if (materialId == null || materialRepository.findById(materialId).isEmpty()) {
            throw new IllegalArgumentException("Неверный ID материала: " + materialId);
        }
        if (makerId == null || makerRepository.findById(makerId).isEmpty()) {
            throw new IllegalArgumentException("Неверный ID производителя: " + makerId);
        }
        if (modelId == null || modelRepository.findById(modelId).isEmpty()) {
            throw new IllegalArgumentException("Неверный ID модели: " + modelId);
        }

        item.setColor(colorRepository.findById(colorId).get());
        item.setMaterial(materialRepository.findById(materialId).get());
        item.setMaker(makerRepository.findById(makerId).get());
        item.setModel(modelRepository.findById(modelId).get());
        itemRepository.save(item);

        // Обновление атрибутов
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String value = entry.getValue();
            Attribute attribute = attributeRepository.findByName(attributeName);
            if (attribute == null) continue;

            ItemAttributeId itemAttributeId = new ItemAttributeId(item.getItemId(), attribute.getAttributeId());
            ItemAttribute itemAttribute = itemAttributeRepository.findById(itemAttributeId)
                    .orElse(new ItemAttribute(itemAttributeId, value));
            itemAttribute.setValue(value);
            itemAttributeRepository.save(itemAttribute);
        }

        // Обработка фотографий для удаления
        if (photosToDelete != null && !photosToDelete.isEmpty()) {
            for (Long photoId : photosToDelete) {
                ItemPhotoLinkId linkId = new ItemPhotoLinkId(item.getItemId(), photoId);
                ItemPhotoLink itemPhotoLink = itemPhotoLinkRepository.findById(linkId)
                        .orElseThrow(() -> new IllegalArgumentException("Фото с ID " + photoId + " не найдено для вещи " + item.getItemId()));

                PhotoLink photoLink = itemPhotoLink.getPhotoLink();
                String url = photoLink.getUrl();

                // Удаление файла
                if (url.startsWith("http")) {
                    // S3
                    String key = url.substring(url.indexOf("items/"));
                    s3Service.deleteFile(key);
                } else if (url.startsWith("items/")) {
                    // Local
                    String filePath = "C:\\Java Projects\\ItemSharing\\src\\main\\resources\\static\\images\\" + url;
                    try {
                        Files.deleteIfExists(Paths.get(filePath));
                    } catch (IOException e) {
                        throw new RuntimeException("Не удалось удалить локальный файл: " + filePath, e);
                    }
                }

                // Удаление записей из БД
                itemPhotoLinkRepository.deleteById(linkId);
                List<ItemPhotoLink> remainingLinks = itemPhotoLinkRepository.findByPhotoLink(photoLink);
                if (remainingLinks.isEmpty()) {
                    photoLinkRepository.delete(photoLink);
                }
            }
        }

        // Обработка новых фотографий
        if (photos != null && photos.length > 0 && !photos[0].isEmpty()) {
            for (MultipartFile photo : photos) {
                if (photo.isEmpty()) continue;
                String originalFilename = photo.getOriginalFilename() != null ? photo.getOriginalFilename() : "photo_" + System.currentTimeMillis() + ".jpg";
                String uniqueFileName = generateUniqueFileName(originalFilename);
                String s3Url = s3Service.uploadFile(uniqueFileName, photo);
                PhotoLink photoLink = new PhotoLink();
                photoLink.setUrl(s3Url);
                photoLinkRepository.save(photoLink);

                ItemPhotoLinkId itemPhotoLinkId = new ItemPhotoLinkId(item.getItemId(), photoLink.getPhotoId());
                ItemPhotoLink itemPhotoLink = new ItemPhotoLink(itemPhotoLinkId, item, photoLink);
                itemPhotoLinkRepository.save(itemPhotoLink);
            }
        }

        // Реиндексация вещи
        itemIndexingService.indexItemAsync(item);

        return "success:" + item.getItemId();
    }

    // Остальные методы без изменений
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Map<String, Object> getItemDetails(String itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        Map<String, Object> modelAttributes = new HashMap<>();

        modelAttributes.put("item", item);
        modelAttributes.put("owner", item.getOwner());

        // Photos
        Map<String, List<String>> photoUrlsMap = new HashMap<>();
        List<ItemPhotoLink> itemPhotoLinks = itemPhotoLinkRepository.findByItem(item);
        List<String> photoUrls = itemPhotoLinks.stream()
                .map(link -> normalizePhotoUrl(link.getPhotoLink().getUrl()))
                .collect(Collectors.toList());
        if (photoUrls.isEmpty()) {
            photoUrls = Collections.singletonList("/images/default.png");
        }
        photoUrlsMap.put(item.getItemId(), photoUrls);
        modelAttributes.put("photoUrlsMap", photoUrlsMap);

        // Reviews
        List<ItemReview> reviews = itemReviewRepository.findByItem(item);
        Map<Long, List<String>> reviewPhotosMap = new HashMap<>();
        for (ItemReview review : reviews) {
            List<ItemReviewPhotoLink> reviewPhotos = itemReviewPhotoLinkRepository.findByItemReview(review);
            List<String> reviewPhotoUrls = reviewPhotos.stream()
                    .map(photoLink -> normalizePhotoUrl(photoLink.getPhotoLink().getUrl()))
                    .collect(Collectors.toList());
            reviewPhotosMap.put(review.getItemReviewId(), reviewPhotoUrls);
        }
        modelAttributes.put("reviews", reviews);
        modelAttributes.put("reviewPhotosMap", reviewPhotosMap);

        // Random color
        modelAttributes.put("randomColor", generateRandomColor());

        // Attributes
        List<ItemAttribute> itemAttributes = itemAttributeRepository.findById_Item(item.getItemId());
        Map<String, String> attributeMap = new HashMap<>();
        for (ItemAttribute itemAttribute : itemAttributes) {
            Attribute attribute = attributeRepository.findById(itemAttribute.getId().getAttribute()).orElseThrow();
            attributeMap.put(attribute.getName(), itemAttribute.getValue());
        }
        modelAttributes.put("attributes", attributeMap);

        // Booked dates
        List<Request> requests = requestRepository.findByItem(item);
        List<String> bookedDates = new ArrayList<>();
        for (Request request : requests) {
            if (request.getStatus().getStatusId() >= 2 && request.getStatus().getStatusId() <= 4) {
                LocalDate startDate = request.getStartDate();
                LocalDate endDate = request.getEndDate();
                while (!startDate.isAfter(endDate)) {
                    bookedDates.add(startDate.toString());
                    startDate = startDate.plusDays(1);
                }
            }
        }
        modelAttributes.put("bookedDates", bookedDates);

        return modelAttributes;
    }

    @Transactional
    public void addReview(String itemId, String comment, int score, MultipartFile[] photos, CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        Item item = itemRepository.findById(itemId).orElseThrow();

        ItemReview review = new ItemReview();
        review.setItem(item);
        review.setReviewer(currentUser);
        review.setComment(comment);
        review.setScore(score);
        review.setDate(LocalDate.now());
        itemReviewRepository.save(review);

        if (photos != null && photos.length > 0) {
            for (MultipartFile photo : photos) {
                if (photo.isEmpty()) continue;
                String originalFilename = photo.getOriginalFilename() != null ? photo.getOriginalFilename() : "photo_" + System.currentTimeMillis() + ".jpg";
                String uniqueFileName = generateUniqueFileName(originalFilename);
                String s3Url = s3Service.uploadFile(uniqueFileName, photo, "reviews/");
                PhotoLink photoLink = new PhotoLink();
                photoLink.setUrl(s3Url);
                photoLinkRepository.save(photoLink);

                ItemReviewPhotoLink reviewPhotoLink = new ItemReviewPhotoLink();
                ItemReviewPhotoLinkId id = new ItemReviewPhotoLinkId(review.getItemReviewId(), photoLink.getPhotoId());
                reviewPhotoLink.setId(id);
                reviewPhotoLink.setItemReview(review);
                reviewPhotoLink.setPhotoLink(photoLink);
                itemReviewPhotoLinkRepository.save(reviewPhotoLink);
            }
        }
    }

    public Map<String, Object> getItemRequestDetails(String itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        Map<String, Object> modelAttributes = new HashMap<>();

        List<Request> requests = requestRepository.findByItem(item);
        List<LocalDate> bookedDates = new ArrayList<>();
        for (Request request : requests) {
            LocalDate startDate = request.getStartDate();
            LocalDate endDate = request.getEndDate();
            while (!startDate.isAfter(endDate)) {
                bookedDates.add(startDate);
                startDate = startDate.plusDays(1);
            }
        }

        modelAttributes.put("bookedDates", bookedDates);
        modelAttributes.put("item", item);
        return modelAttributes;
    }

    @Transactional
    public String createRequest(String itemId, LocalDate startDate, LocalDate endDate, CustomUserDetails userDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        User currentUser = userDetails.getUser();

        List<Request> requests = requestRepository.findByItem(item);
        List<LocalDate> bookedDates = new ArrayList<>();
        for (Request request : requests) {
            if (request.getStatus().getStatusId() >= 2 && request.getStatus().getStatusId() <= 4) {
                LocalDate startDateFor = request.getStartDate();
                LocalDate endDateFor = request.getEndDate();
                while (!startDateFor.isAfter(endDateFor)) {
                    bookedDates.add(startDateFor);
                    startDateFor = startDateFor.plusDays(1);
                }
            }
        }

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (bookedDates.contains(date)) {
                return "error:Выбранные даты уже забронированы";
            }
        }

        RequestStatus status = requestStatusRepository.findById(1L).orElseThrow();
        Request request = new Request();
        request.setHolder(currentUser);
        request.setItem(item);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setStatus(status);
        requestRepository.save(request);

        return "success";
    }

    public Map<String, Object> getCreateItemFormData(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        Map<String, Object> modelAttributes = new HashMap<>();

        Item newItem = new Item();
        newItem.setCategory(category);
        modelAttributes.put("newItem", newItem);
        modelAttributes.put("category", category);

        List<Attribute> attributes = categoryAttributeRepository.findById_CategoryId(categoryId).stream()
                .map(catAttr -> attributeRepository.findById(catAttr.getId().getAttributeId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Long, List<String>> enumValuesMap = new HashMap<>();
        for (Attribute attribute : attributes) {
            if (attribute.getType() == AttributeType.ENUM) {
                List<String> values = attributeEnumValueRepository.findById_AttributeId(attribute.getAttributeId())
                        .stream()
                        .map(value -> value.getId().getValue())
                        .collect(Collectors.toList());
                enumValuesMap.put(attribute.getAttributeId(), values);
            }
        }

        modelAttributes.put("categoryAttributes", attributes);
        modelAttributes.put("enumValuesMap", enumValuesMap);
        modelAttributes.put("categories", categoryRepository.findAll());
        modelAttributes.put("colors", colorRepository.findAll());
        modelAttributes.put("materials", materialRepository.findAll());
        modelAttributes.put("makers", makerRepository.findAll());
        modelAttributes.put("models", modelRepository.findAll());

        return modelAttributes;
    }

    @Transactional
    public String saveItem(Item newItem, Long categoryId, Map<String, String> attributes, MultipartFile[] photos,
                           Long colorId, Long materialId, Long makerId, Long modelId, CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        newItem.setAvailable(true);
        newItem.setCategory(categoryRepository.findById(categoryId).orElseThrow());
        newItem.setOwner(currentUser);
        newItem.setColor(colorRepository.findById(colorId).orElseThrow());
        newItem.setMaterial(materialRepository.findById(materialId).orElseThrow());
        newItem.setMaker(makerRepository.findById(makerId).orElseThrow());
        newItem.setModel(modelRepository.findById(modelId).orElseThrow());

        itemRepository.save(newItem);

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String value = entry.getValue();
            if (attributeName.matches("^[a-zA-Z]+$")) continue;

            Attribute attribute = attributeRepository.findByName(attributeName);
            if (attribute == null) continue;

            ItemAttributeId itemAttributeId = new ItemAttributeId(newItem.getItemId(), attribute.getAttributeId());
            ItemAttribute itemAttribute = new ItemAttribute();
            itemAttribute.setId(itemAttributeId);
            itemAttribute.setValue(value);
            itemAttributeRepository.save(itemAttribute);
        }

        if (photos != null && photos.length > 0 && !photos[0].isEmpty()) {
            for (MultipartFile photo : photos) {
                if (photo.isEmpty()) continue;
                String originalFilename = photo.getOriginalFilename() != null ? photo.getOriginalFilename() : "photo_" + System.currentTimeMillis() + ".jpg";
                String uniqueFileName = generateUniqueFileName(originalFilename);
                String s3Url = s3Service.uploadFile(uniqueFileName, photo);
                PhotoLink photoLink = new PhotoLink();
                photoLink.setUrl(s3Url);
                photoLinkRepository.save(photoLink);

                ItemPhotoLinkId itemPhotoLinkId = new ItemPhotoLinkId(newItem.getItemId(), photoLink.getPhotoId());
                ItemPhotoLink itemPhotoLink = new ItemPhotoLink(itemPhotoLinkId, newItem, photoLink);
                itemPhotoLinkRepository.save(itemPhotoLink);
            }
        }

        itemIndexingService.indexItemAsync(newItem);

        return "success:" + newItem.getItemId();
    }

    @Transactional
    public void deleteItem(String itemId) {
        itemAttributeRepository.deleteById_Item(itemId);
        itemPhotoLinkRepository.deleteByItem_ItemId(itemId);
        itemRepository.deleteById(itemId);
    }

    private String normalizePhotoUrl(String url) {
        if (url.startsWith("http")) {
            return url;
        } else if (url.startsWith("items/")) {
            return "/images/" + url;
        }
        return "/images/default.png";
    }

    private String generateUniqueFileName(String originalFilename) {
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return baseName + "_" + System.currentTimeMillis() + extension;
    }

    private String generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("#%02X%02X%02X", r, g, b);
    }
}