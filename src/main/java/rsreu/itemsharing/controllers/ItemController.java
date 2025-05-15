package rsreu.itemsharing.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.security.CustomUserDetails;
import rsreu.itemsharing.services.ItemService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;

    @GetMapping("/item/{itemId}")
    public String getItem(@PathVariable("itemId") String itemId, Model model) {
        Map<String, Object> attributes = itemService.getItemDetails(itemId);
        model.addAllAttributes(attributes);
        return "itemCard";
    }

    @PostMapping("/item/{itemId}/review")
    public String addReview(@PathVariable("itemId") String itemId,
                            @RequestParam("comment") String comment,
                            @RequestParam("score") int score,
                            @RequestParam("photo") MultipartFile[] photos) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        itemService.addReview(itemId, comment, score, photos, userDetails);
        return "redirect:/item/" + itemId;
    }

    @GetMapping("/item/{itemId}/request")
    public String getItemRequest(@PathVariable("itemId") String itemId, Model model) {
        Map<String, Object> attributes = itemService.getItemRequestDetails(itemId);
        model.addAllAttributes(attributes);
        return "itemRequest";
    }

    @PostMapping("/item/{itemId}/request")
    public String createRequest(@PathVariable("itemId") String itemId,
                                @RequestParam("startDate") LocalDate startDate,
                                @RequestParam("endDate") LocalDate endDate,
                                Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = itemService.createRequest(itemId, startDate, endDate, userDetails);
        if (result.startsWith("error")) {
            model.addAttribute("error", result.substring(6));
            model.addAllAttributes(itemService.getItemDetails(itemId));
            return "itemCard";
        }
        return "redirect:/item/" + itemId;
    }

    @GetMapping("/selectCategory")
    public String selectCategory(Model model) {
        List<Category> categories = itemService.getAllCategories();
        model.addAttribute("categories", categories);
        return "selectCategory";
    }

    @GetMapping("/createItem")
    public String createItemForm(@RequestParam(name = "category", required = true) Long category, Model model) {
        Map<String, Object> attributes = itemService.getCreateItemFormData(category);
        model.addAllAttributes(attributes);
        return "createItem";
    }

    @PostMapping("/saveItem")
    public String saveItem(@ModelAttribute Item newItem,
                           @RequestParam Long categoryId,
                           @RequestParam Map<String, String> attributes,
                           @RequestParam("photos") MultipartFile[] photos,
                           @RequestParam("colorId") Long colorId,
                           @RequestParam("materialId") Long materialId,
                           @RequestParam("makerId") Long makerId,
                           @RequestParam("modelId") Long modelId,
                           Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = itemService.saveItem(newItem, categoryId, attributes, photos, colorId, materialId, makerId, modelId, userDetails);
        if (result.startsWith("error")) {
            model.addAttribute("error", result.substring(6));
            return "error";
        }
        return "redirect:/item/" + result.split(":")[1];
    }

    @GetMapping("/editItem/{itemId}")
    public String editItemForm(@PathVariable String itemId, Model model) {
        Map<String, Object> attributes = itemService.getEditItemFormData(itemId);
        model.addAllAttributes(attributes);
        return "editItem";
    }

    @PostMapping("/updateItem")
    public String updateItem(@ModelAttribute Item updatedItem,
                             @RequestParam Map<String, String> attributes,
                             @RequestParam(value = "photos", required = false) MultipartFile[] photos,
                             @RequestParam("colorId") Long colorId,
                             @RequestParam("materialId") Long materialId,
                             @RequestParam("makerId") Long makerId,
                             @RequestParam("modelId") Long modelId,
                             @RequestParam(value = "photosToDelete", required = false) String photosToDelete,
                             Model model) {
        List<Long> photosToDeleteList = photosToDelete != null && !photosToDelete.isEmpty()
                ? Arrays.stream(photosToDelete.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList())
                : Collections.emptyList();

        String result = itemService.updateItem(updatedItem, attributes, photos != null ? photos : new MultipartFile[0], colorId, materialId, makerId, modelId, photosToDeleteList);
        if (result.startsWith("error")) {
            model.addAttribute("error", result.substring(6));
            model.addAllAttributes(itemService.getEditItemFormData(updatedItem.getItemId()));
            return "editItem";
        }
        return "redirect:/item/" + result.split(":")[1];
    }

    @PostMapping("/deleteItem/{itemId}")
    public String deleteItem(@PathVariable String itemId) {
        itemService.deleteItem(itemId);
        return "redirect:/";
    }
}