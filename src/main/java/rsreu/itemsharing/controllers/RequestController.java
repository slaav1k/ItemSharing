package rsreu.itemsharing.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.entities.Request;
import rsreu.itemsharing.entities.RequestStatus;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.repositories.RequestRepository;
import rsreu.itemsharing.repositories.UserRepository;
import rsreu.itemsharing.security.CustomUserDetails;

import java.security.Principal;
import java.util.List;

@Controller
public class RequestController {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public RequestController(final RequestRepository requestRepository, final UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/requests")
    public String showRequests(Model model, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = customUserDetails.getUser();

        List<Request> outgoingRequests = requestRepository.findByHolder(user);
        List<Request> incomingRequests = requestRepository.findByItemOwner(user);

        model.addAttribute("outgoingRequests", outgoingRequests);
        model.addAttribute("incomingRequests", incomingRequests);
        model.addAttribute("user", user);
        return "requests";
    }

    @PostMapping("/requests/cancel")
    public String cancelRequest(@RequestParam Long requestId) {
        requestRepository.deleteById(requestId);
        return "redirect:/requests";
    }

    @PostMapping("/requests/approve")
    public String approveRequest(@RequestParam Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        request.setStatus(new RequestStatus(2, "Approved", "Заявка одобрена."));
        requestRepository.save(request);
        return "redirect:/requests";
    }

    @PostMapping("/requests/reject")
    public String rejectRequest(@RequestParam Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        request.setStatus(new RequestStatus(5, "Rejected", "Заявка отклонена."));
        requestRepository.save(request);
        return "redirect:/requests";
    }
}
