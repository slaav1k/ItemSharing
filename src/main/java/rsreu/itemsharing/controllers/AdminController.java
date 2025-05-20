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
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.repositories.ItemRepository;
import rsreu.itemsharing.repositories.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
public class AdminController {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
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

    @GetMapping("/manageUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "adminManageUsers";
    }

    @PostMapping("/user/update-role")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUserRole(@RequestParam Long passportNum, @RequestParam String role) {
        User user = userRepository.findById(passportNum)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (List.of("USER", "MODERATOR", "ADMIN").contains(role)) {
            user.setRole("ROLE_" + role);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
        return "redirect:/admin/manageUsers";
    }
}