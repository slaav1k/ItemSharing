package rsreu.itemsharing.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private final PasswordEncoder passwordEncoder;


    public SettingsController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        currentUser.setPassword(passwordEncoder.encode(password));

        userRepository.save(currentUser);

        return "redirect:/settings";
    }

    // Обработка удаления аккаунта пользователя
//    @PostMapping("/settings/delete")
//    public String deleteAccount(Principal principal) {
////        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
////        User currentUser = customUserDetails.getUser();
////        userRepository.delete(currentUser);
//        return "redirect:/"; // Перенаправление на страницу выхода
//    }

    @PostMapping("/settings/delete")
    @Transactional
    public String deleteAccount(HttpServletRequest request, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();
        userRepository.delete(currentUser);
        request.getSession().invalidate();
        return "redirect:/login?logout";
    }



}
