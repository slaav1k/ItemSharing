package rsreu.itemsharing.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.Request;
import rsreu.itemsharing.entities.RequestStatus;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.infrastructure.Birt;
import rsreu.itemsharing.repositories.RequestRepository;
import rsreu.itemsharing.repositories.RequestStatusRepository;
import rsreu.itemsharing.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestStatusRepository requestStatusRepository;

    @Autowired
    private Birt birt;

    public List<Request> getOutgoingRequests(User user) {
        return requestRepository.findByHolder(user);
    }

    public List<Request> getIncomingRequests(User user) {
        return requestRepository.findByItemOwner(user);
    }

    public void updateRequestStatus(Long requestId, Long statusId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        RequestStatus status = requestStatusRepository.findById(statusId).orElseThrow(() -> new RuntimeException("Status not found"));
        request.setStatus(status);
        requestRepository.save(request);
    }
}