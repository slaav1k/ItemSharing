package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemPhotoLink;
import rsreu.itemsharing.entities.ItemReview;
import rsreu.itemsharing.entities.ItemReviewPhotoLink;
import rsreu.itemsharing.repositories.ItemPhotoLinkRepository;
import rsreu.itemsharing.repositories.ItemRepository;
import rsreu.itemsharing.repositories.ItemReviewPhotoLinkRepository;
import rsreu.itemsharing.repositories.ItemReviewRepository;

import java.util.*;

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

        return "itemCard";
    }

    private String generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
