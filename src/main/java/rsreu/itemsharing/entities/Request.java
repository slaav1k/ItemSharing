package rsreu.itemsharing.entities;

import java.time.LocalDate;

public class Request {
    private Long requestId;
    private Long holder;
    private String item;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long status;

    public Request() {

    }

    public Request(Long requestId, Long holder, String item, LocalDate startDate, LocalDate endDate, Long status) {
        this.requestId = requestId;
        this.holder = holder;
        this.item = item;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }



}
