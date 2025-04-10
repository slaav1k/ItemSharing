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

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private Birt birt;

    private final UserRepository userRepository;
    private final ItemPhotoLinkRepository itemPhotoLinkRepository;
    private final RequestRepository requestRepository;
    private final ReviewRepository reviewRepository;
    private final BlackListRepository blackListRepository;
    private final ReportService reportService;

    public UserController(UserRepository userRepository, ItemPhotoLinkRepository itemPhotoLinkRepository, RequestRepository requestRepository, ReviewRepository reviewRepository, BlackListRepository blackListRepository, ReportService reportService) {
        this.userRepository = userRepository;
        this.itemPhotoLinkRepository = itemPhotoLinkRepository;
        this.requestRepository = requestRepository;
        this.reviewRepository = reviewRepository;
        this.blackListRepository = blackListRepository;
        this.reportService = reportService;
    }

    @GetMapping("/{passportNum}")
    public String getUserProfile(@PathVariable("passportNum") long passportNum, Model model) {
        Optional<User> userOptional  = userRepository.findByPassportNum(passportNum);
        User user = userOptional.orElse(null);

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        boolean isBlocked = checkIfBlocked(currentUser, user);

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

        List<Request> itemsInUse = getItemsInUse(user);

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
    public String rateUser(@PathVariable Long passportNum, @RequestParam int score, @RequestParam String comment, Principal principal) {
        // Логика для создания и сохранения отзыва

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User reviewer = customUserDetails.getUser();

        Optional<User> userOptional  = userRepository.findByPassportNum(passportNum);
        User reviewedUser = userOptional.orElse(null);
        LocalDate currentDate = LocalDate.now();

        ReviewId reviewId = new ReviewId(passportNum, reviewer.getPassportNum(), currentDate);
        Review review = new Review(reviewId, reviewedUser, reviewer, comment, score);

        reviewRepository.save(review); // Сохранить отзыв в базе данных

        return "redirect:/user/{passportNum}"; // Перенаправить на страницу профиля
    }

    @PostMapping("/blockUser/{passportNum}")
    public String blockUser(@PathVariable Long passportNum, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        Optional<User> userOptional  = userRepository.findByPassportNum(passportNum);
        User targetUser = userOptional.orElse(null);

        if (targetUser != null) {
            LocalDate currentDate = LocalDate.now();
            BlackListId blackListId = new BlackListId(targetUser.getPassportNum(), currentUser.getPassportNum());

            BlackList blackList = new BlackList(blackListId, targetUser, currentUser, currentDate);
            blackListRepository.save(blackList);
        }

        return "redirect:/user/{passportNum}";
    }

    @PostMapping("/unblockUser/{passportNum}")
    @Transactional
    public String unblockUser(@PathVariable Long passportNum, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        Optional<User> userOptional  = userRepository.findByPassportNum(passportNum);
        User targetUser = userOptional.orElse(null);

        if (targetUser != null) {
            blackListRepository.deleteByBlockedUserEntityAndBlockedByUserEntity(targetUser, currentUser);
        }

        return "redirect:/user/{passportNum}";
    }

    @GetMapping("/blacklist")
    public String getBlacklist(Model model, Principal principal) {
        // Получаем текущего пользователя
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        // Получаем список пользователей, которых заблокировал текущий пользователь
        List<BlackList> blockedByCurrentUser = blackListRepository.findByBlockedByUserEntity(currentUser);

        // Получаем список пользователей, которые заблокировали текущего пользователя
        List<BlackList> blockingCurrentUser = blackListRepository.findByBlockedUserEntity(currentUser);

        // Передаем данные в модель
        model.addAttribute("blockedByCurrentUser", blockedByCurrentUser);
        model.addAttribute("blockingCurrentUser", blockingCurrentUser);
        model.addAttribute("user", currentUser);
        return "blacklist"; // Имя страницы для отображения черного списка
    }


    private boolean checkIfBlocked(User currentUser, User targetUser) {
        Optional<BlackList> blackListEntry = blackListRepository.findByBlockedUserEntityAndBlockedByUserEntity(targetUser, currentUser);
        return blackListEntry.isPresent();
    }


    @GetMapping("/downloadBlacklistReport")
    public void downloadBlacklistReport(HttpServletResponse response,
                                        HttpServletRequest request,
                                        Principal principal) throws IOException {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=blacklist_report.pdf");
//
//
//        List<BlackList> blockedBy = blackListRepository.findByBlockedByUserEntity(user);
//        List<BlackList> blocked = blackListRepository.findByBlockedUserEntity(user);
//
//        reportService.generateBlacklistReport(response, blocked, blockedBy);
        birt.generatePDF_aboutBlacklistByUser(user.getPassportNum(), response, request);
    }


    private List<Request> getItemsInUse(User user) {
        List<Request> itemsInUse = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Получаем все заявки текущего пользователя
        List<Request> bookingRequests = requestRepository.findByHolder(user);

        // Фильтруем заявки, где статус = 2 (одобрено) и текущая дата входит в диапазон дат заявки
        for (Request request : bookingRequests) {
            if (request.getStatus().getStatusId() == 3
                    && !today.isBefore(request.getStartDate()) && !today.isAfter(request.getEndDate())) {
                itemsInUse.add(request);  // Добавляем заявку, если статус "одобрено" и текущая дата в пределах диапазона
            }
        }
        return itemsInUse;
    }

    private double calculateAverageScore(User user) {
        List<Review> reviews = user.getReviewsReceived();
        return reviews.isEmpty()
                ? 0.0
                : reviews.stream().mapToInt(Review::getScore).average().orElse(0.0);
    }

}
