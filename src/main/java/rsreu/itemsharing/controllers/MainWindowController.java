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
        model.addAttribute("categories", categoryRepository.findAll());
        if (category != null) {
            model.addAttribute("category", category);
        }


        return "catalog";
    }

}
