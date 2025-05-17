package rsreu.itemsharing.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.infrastructure.Birt;
import rsreu.itemsharing.security.CustomUserDetails;
import rsreu.itemsharing.services.RequestService;

import java.security.Principal;

@Controller
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private Birt birt;

    @GetMapping("/requests")
    public String showRequests(Model model, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = customUserDetails.getUser();

        model.addAttribute("outgoingRequests", requestService.getOutgoingRequests(user));
        model.addAttribute("incomingRequests", requestService.getIncomingRequests(user));
        model.addAttribute("user", user);
        return "requests";
    }

    @PostMapping("/requests/cancel")
    public String cancelRequest(@RequestParam Long requestId) {
        requestService.updateRequestStatus(requestId, 7L); // Статус "Отменена"
        return "redirect:/requests";
    }

    @PostMapping("/requests/approve")
    public String approveRequest(@RequestParam Long requestId,
                                 @RequestParam(value = "confirmed", defaultValue = "false") boolean confirmed,
                                 Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (!confirmed) {
            model.addAttribute("outgoingRequests", requestService.getOutgoingRequests(user));
            model.addAttribute("incomingRequests", requestService.getIncomingRequests(user));
            model.addAttribute("user", user);
            return "requests";
        }

        requestService.updateRequestStatus(requestId, 2L); // Статус "Одобрена"
        return "redirect:/requests";
    }

    @PostMapping("/requests/confirmReceipt")
    public String confirmReceipt(@RequestParam Long requestId) {
        requestService.updateRequestStatus(requestId, 3L); // Статус "Вещь в пользовании"
        return "redirect:/requests";
    }

    @PostMapping("/requests/makeReturn")
    public String makeReturn(@RequestParam Long requestId) {
        requestService.updateRequestStatus(requestId, 4L); // Статус "Ожидает возврат вещи"
        return "redirect:/requests";
    }

    @PostMapping("/requests/requestReturn")
    public String requestReturn(@RequestParam Long requestId) {
        requestService.updateRequestStatus(requestId, 4L); // Статус "Ожидает возврат вещи"
        return "redirect:/requests";
    }

    @PostMapping("/requests/confirmReturn")
    public String confirmReturn(@RequestParam Long requestId) {
        requestService.updateRequestStatus(requestId, 5L); // Статус "Заявка выполнена"
        return "redirect:/requests";
    }

    @PostMapping("/requests/reject")
    public String rejectRequest(@RequestParam Long requestId) {
        requestService.updateRequestStatus(requestId, 6L); // Статус "Отклонена"
        return "redirect:/requests";
    }

    @GetMapping("/downloadRequestsReport")
    public void downloadRequestsReport(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Principal principal) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
//
//
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=requests_report.pdf");
//
//        List<Request> incoming = requestRepository.findByItemOwner(user);
//        List<Request> outgoing = requestRepository.findByHolder(user);
//
//        reportService.generateRequestsReport(response, incoming, outgoing);
        birt.generatePDF_aboutRequestsByUser(user.getPassportNum(), response, request);
    }
}