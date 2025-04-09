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
import rsreu.itemsharing.entities.Request;
import rsreu.itemsharing.entities.RequestStatus;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.laba4.Birt;
import rsreu.itemsharing.repositories.RequestRepository;
import rsreu.itemsharing.repositories.RequestStatusRepository;
import rsreu.itemsharing.repositories.UserRepository;
import rsreu.itemsharing.security.CustomUserDetails;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class RequestController {

    @Autowired
    private Birt birt;

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ReportService reportService;
    private final RequestStatusRepository requestStatusRepository;

    public RequestController(final RequestRepository requestRepository, final UserRepository userRepository, ReportService reportService, RequestStatusRepository requestStatusRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.reportService = reportService;
        this.requestStatusRepository = requestStatusRepository;
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
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestStatus status = requestStatusRepository.findById(7L).orElseThrow();
        request.setStatus(status);
        requestRepository.save(request);
        return "redirect:/requests";
    }

    @PostMapping("/requests/approve")
    public String approveRequest(@RequestParam Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestStatus status = requestStatusRepository.findById(2L).orElseThrow();
        request.setStatus(status);
        requestRepository.save(request);
        return "redirect:/requests";
    }

    @PostMapping("/requests/confirmReceipt")
    public String confirmReceipt(@RequestParam Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestStatus status = requestStatusRepository.findById(3L).orElseThrow(); // Статус: "Вещь в пользовании"
        request.setStatus(status);
        requestRepository.save(request);
        return "redirect:/requests";
    }

    @PostMapping("/requests/makeReturn")
    public String makeReturn(@RequestParam Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestStatus status = requestStatusRepository.findById(4L).orElseThrow(); // Статус: "Вещь в пользовании"
        request.setStatus(status);
        requestRepository.save(request);
        return "redirect:/requests";
    }

    @PostMapping("/requests/requestReturn")
    public String requestReturn(@RequestParam Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestStatus status = requestStatusRepository.findById(4L).orElseThrow(); // "Ожидает возврат вещи"
        request.setStatus(status);
        requestRepository.save(request);
        return "redirect:/requests";
    }

    @PostMapping("/requests/confirmReturn")
    public String confirmReturn(@RequestParam Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestStatus status = requestStatusRepository.findById(5L).orElseThrow(); // "Заявка выполнена. Вещь у владельца"
        request.setStatus(status);
        requestRepository.save(request);
        return "redirect:/requests";
    }



    @PostMapping("/requests/reject")
    public String rejectRequest(@RequestParam Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestStatus status = requestStatusRepository.findById(6L).orElseThrow();
        request.setStatus(status);
        requestRepository.save(request);
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
