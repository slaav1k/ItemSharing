package rsreu.itemsharing.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.*;
import rsreu.itemsharing.laba4.Birt;
import rsreu.itemsharing.repositories.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private Birt birt;

    private final UserRepository userRepository;
    private final ItemPhotoLinkRepository itemPhotoLinkRepository;
    private final RequestRepository requestRepository;
    private final ReviewRepository reviewRepository;
    private final BlackListRepository blackListRepository;

    public UserService(UserRepository userRepository,
                       ItemPhotoLinkRepository itemPhotoLinkRepository,
                       RequestRepository requestRepository,
                       ReviewRepository reviewRepository,
                       BlackListRepository blackListRepository) {
        this.userRepository = userRepository;
        this.itemPhotoLinkRepository = itemPhotoLinkRepository;
        this.requestRepository = requestRepository;
        this.reviewRepository = reviewRepository;
        this.blackListRepository = blackListRepository;
    }

    public Optional<User> findUserByPassportNum(long passportNum) {
        return userRepository.findByPassportNum(passportNum);
    }

    public boolean isBlocked(User currentUser, User targetUser) {
        return blackListRepository.findByBlockedUserEntityAndBlockedByUserEntity(targetUser, currentUser).isPresent();
    }

    public double calculateAverageScore(User user) {
        List<Review> reviews = user.getReviewsReceived();
        return reviews.isEmpty() ? 0.0 : reviews.stream().mapToInt(Review::getScore).average().orElse(0.0);
    }

    public Map<String, List<String>> buildPhotoUrlsMap(List<Item> items) {
        Map<String, List<String>> map = new HashMap<>();
        for (Item item : items) {
            List<String> photoUrls = itemPhotoLinkRepository.findByItem(item)
                    .stream()
                    .map(link -> normalizePhotoUrl(link.getPhotoLink().getUrl()))
                    .toList();
            // Если у товара нет фотографий, добавляем заглушку
            if (photoUrls.isEmpty()) {
                photoUrls = Collections.singletonList("/images/default.png");
            }
            map.put(item.getItemId(), photoUrls);
        }
        return map;
    }

    private String normalizePhotoUrl(String url) {
        if (url.startsWith("http")) {
            return url;
        } else if (url.startsWith("items/")) {
            return "/images/" + url;
        }
        return "/images/default.png";
    }

    public List<Request> getItemsInUse(User user) {
        List<Request> itemsInUse = new ArrayList<>();
        LocalDate today = LocalDate.now();
        List<Request> bookingRequests = requestRepository.findByHolder(user);
        for (Request request : bookingRequests) {
            if (request.getStatus().getStatusId() == 3 &&
                    !today.isBefore(request.getStartDate()) &&
                    !today.isAfter(request.getEndDate())) {
                itemsInUse.add(request);
            }
        }
        return itemsInUse;
    }

    public void saveReview(User reviewer, User reviewed, int score, String comment) {
        ReviewId reviewId = new ReviewId(reviewed.getPassportNum(), reviewer.getPassportNum(), LocalDate.now());
        Review review = new Review(reviewId, reviewed, reviewer, comment, score);
        reviewRepository.save(review);
    }

    public void blockUser(User targetUser, User currentUser) {
        BlackListId blackListId = new BlackListId(targetUser.getPassportNum(), currentUser.getPassportNum());
        BlackList blackList = new BlackList(blackListId, targetUser, currentUser, LocalDate.now());
        blackListRepository.save(blackList);
    }

    public void unblockUser(User targetUser, User currentUser) {
        blackListRepository.deleteByBlockedUserEntityAndBlockedByUserEntity(targetUser, currentUser);
    }

    public List<BlackList> getBlockedBy(User user) {
        return blackListRepository.findByBlockedByUserEntity(user);
    }

    public List<BlackList> getBlocking(User user) {
        return blackListRepository.findByBlockedUserEntity(user);
    }

    public void generateBlacklistReport(User user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        birt.generatePDF_aboutBlacklistByUser(user.getPassportNum(), response, request);
    }
}
