package rsreu.itemsharing.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.repositories.UserRepository;
import rsreu.itemsharing.security.CustomUserDetails;

import java.security.Principal;

@Controller
public class SettingsController {
    private final UserRepository userRepository;


    public SettingsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/settings")
    public String showSettingsPage(Model model, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();
        model.addAttribute("user", currentUser);
        return "settings";
    }

    @PostMapping("/settings/update")
    public String updateSettings(@RequestParam String fullName,
                                 @RequestParam String phone,
                                 @RequestParam String email,
                                 @RequestParam String address,
                                 @RequestParam String password,
                                 Principal principal) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        currentUser.setFullName(fullName);
        currentUser.setPhone(phone);
        currentUser.setEmail(email);
        currentUser.setAddress(address);
        currentUser.setPassword(password);

        userRepository.save(currentUser);

        return "redirect:/settings";
    }

    // Обработка удаления аккаунта пользователя
    @PostMapping("/settings/delete")
    public String deleteAccount(Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();
        userRepository.delete(currentUser);
        return "redirect:/logout"; // Перенаправление на страницу выхода
    }


}
