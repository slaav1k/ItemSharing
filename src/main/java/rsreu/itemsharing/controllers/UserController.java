package rsreu.itemsharing.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.laba4.Birt;
import rsreu.itemsharing.repositories.*;
import rsreu.itemsharing.security.CustomUserDetails;
import rsreu.itemsharing.services.UserService;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ReportService reportService;

    public UserController(UserService userService, ReportService reportService) {
        this.userService = userService;
        this.reportService = reportService;
    }

    @GetMapping("/{passportNum}")
    public String getUserProfile(@PathVariable("passportNum") long passportNum, Model model) {
        Optional<User> userOptional = userService.findUserByPassportNum(passportNum);
        User user = userOptional.orElse(null);

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        boolean isBlocked = userService.isBlocked(currentUser, user);
        double averageScore = userService.calculateAverageScore(user);
        List<Item> items = user.getItems();
        Map<String, List<String>> photoUrlsMap = userService.buildPhotoUrlsMap(items);
        List<Request> itemsInUse = userService.getItemsInUse(user);

        model.addAttribute("user", user);
        model.addAttribute("items", items);
        model.addAttribute("reviews", user.getReviewsReceived());
        model.addAttribute("photoUrlsMap", photoUrlsMap);
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("itemsInUse", itemsInUse);
        model.addAttribute("isBlocked", isBlocked);
        return "userProfile";
    }

    @PostMapping("/rateUser/{passportNum}")
    public String rateUser(@PathVariable Long passportNum, @RequestParam int score, @RequestParam String comment) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User reviewer = customUserDetails.getUser();
        Optional<User> userOptional = userService.findUserByPassportNum(passportNum);
        userOptional.ifPresent(reviewed -> userService.saveReview(reviewer, reviewed, score, comment));
        return "redirect:/user/{passportNum}";
    }

    @PostMapping("/blockUser/{passportNum}")
    public String blockUser(@PathVariable Long passportNum) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();
        Optional<User> userOptional = userService.findUserByPassportNum(passportNum);
        userOptional.ifPresent(targetUser -> userService.blockUser(targetUser, currentUser));
        return "redirect:/user/{passportNum}";
    }

    @PostMapping("/unblockUser/{passportNum}")
    @Transactional
    public String unblockUser(@PathVariable Long passportNum) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();
        Optional<User> userOptional = userService.findUserByPassportNum(passportNum);
        userOptional.ifPresent(targetUser -> userService.unblockUser(targetUser, currentUser));
        return "redirect:/user/{passportNum}";
    }

    @GetMapping("/blacklist")
    public String getBlacklist(Model model) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        model.addAttribute("blockedByCurrentUser", userService.getBlockedBy(currentUser));
        model.addAttribute("blockingCurrentUser", userService.getBlocking(currentUser));
        model.addAttribute("user", currentUser);
        return "blacklist";
    }

    @GetMapping("/downloadBlacklistReport")
    public void downloadBlacklistReport(HttpServletResponse response, HttpServletRequest request) throws IOException {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        userService.generateBlacklistReport(user, request, response);
    }
}
