package rsreu.itemsharing.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "holder", nullable = false)
    private User holder;

    @ManyToOne
    @JoinColumn(name = "item", nullable = false)
    private Item item;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private RequestStatus status;


    public Request() {
    }

    public Request(Long requestId, User holder, Item item, LocalDate startDate, LocalDate endDate, RequestStatus status) {
        this.requestId = requestId;
        this.holder = holder;
        this.item = item;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public User getHolder() {
        return holder;
    }

    public void setHolder(User holder) {
        this.holder = holder;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
