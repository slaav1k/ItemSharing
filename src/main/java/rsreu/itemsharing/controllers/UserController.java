package rsreu.itemsharing.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemPhotoLink;
import rsreu.itemsharing.entities.Review;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.repositories.ItemPhotoLinkRepository;
import rsreu.itemsharing.repositories.UserRepository;

import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final ItemPhotoLinkRepository itemPhotoLinkRepository;

    public UserController(UserRepository userRepository, ItemPhotoLinkRepository itemPhotoLinkRepository) {
        this.userRepository = userRepository;
        this.itemPhotoLinkRepository = itemPhotoLinkRepository;
    }

    @GetMapping("/{passportNum}")
    public String getUserProfile(@PathVariable("passportNum") long passportNum, Model model) {
        Optional<User> userOptional  = userRepository.findByPassportNum(passportNum);
        User user = userOptional.orElse(null);

        double averageScore = calculateAverageScore(user);

        List<Item> items = user.getItems();

        Map<String, List<String>> photoUrlsMap = new HashMap<>();
        for (Item item : items) {
            List<ItemPhotoLink> itemPhotoLinks = itemPhotoLinkRepository.findByItem(item);
            List<String> photoUrls = new ArrayList<>();
            for (ItemPhotoLink itemPhotoLink : itemPhotoLinks) {
                photoUrls.add(itemPhotoLink.getPhotoLink().getUrl());
            }
            photoUrlsMap.put(item.getItemId(), photoUrls);
        }

        model.addAttribute("user", user);
        model.addAttribute("items", items);
        model.addAttribute("reviews", user.getReviewsReceived());
        model.addAttribute("photoUrlsMap", photoUrlsMap);
        model.addAttribute("averageScore", averageScore);
        return "userProfile";
    }

    public double calculateAverageScore(User user) {
        List<Review> reviews = user.getReviewsReceived();
        return reviews.isEmpty()
                ? 0.0
                : reviews.stream().mapToInt(Review::getScore).average().orElse(0.0);
    }

}
