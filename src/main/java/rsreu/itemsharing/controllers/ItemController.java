package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemPhotoLink;
import rsreu.itemsharing.repositories.ItemPhotoLinkRepository;
import rsreu.itemsharing.repositories.ItemRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemPhotoLinkRepository itemPhotoLinkRepository;

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
        return "itemCard";
    }
}
