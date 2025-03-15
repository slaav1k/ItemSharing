package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.entities.Category;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemPhotoLink;
import rsreu.itemsharing.repositories.CategoryRepository;
import rsreu.itemsharing.repositories.ItemPhotoLinkRepository;
import rsreu.itemsharing.repositories.ItemRepository;

import java.sql.*;
import java.util.*;

@Controller("/")
public class MainWindowController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemPhotoLinkRepository itemPhotoLinkRepository;

    @GetMapping()
    public String catalog(@RequestParam(required = false) Long category, Model model) {
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

        Map<String, List<String>> photoUrlsMap = new HashMap<>();


        for (Item item : items) {
            List<ItemPhotoLink> itemPhotoLinks = itemPhotoLinkRepository.findByItem(item);
            List<String> photoUrls = new ArrayList<>();
            for (ItemPhotoLink itemPhotoLink : itemPhotoLinks) {
                photoUrls.add(itemPhotoLink.getPhotoLink().getUrl());
            }
            photoUrlsMap.put(item.getItemId(), photoUrls);
        }

        model.addAttribute("items", items);
        model.addAttribute("photoUrlsMap", photoUrlsMap);

        return "catalog";
    }



}
