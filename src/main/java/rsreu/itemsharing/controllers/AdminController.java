package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.repositories.ItemRepository;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ItemRepository itemRepository;

    @Autowired
    public AdminController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<Item> recentItems = itemRepository.findAllByOrderByUpdatedAtDesc();
        model.addAttribute("items", recentItems);
        return "admin-dashboard";
    }

    @PostMapping("/item/block")
    public String blockItem(@RequestParam String itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        item.setBlocked(true);
        itemRepository.save(item);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/item/unblock")
    public String unblockItem(@RequestParam String itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        item.setBlocked(false);
        itemRepository.save(item);
        return "redirect:/admin/dashboard";
    }
}