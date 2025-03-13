package rsreu.itemsharing.entities;

public class RequestStatus {
    private long statusId;
    private String name;
    private String description;

    public RequestStatus(long statusId, String name, String description) {
        this.statusId = statusId;
        this.name = name;
        this.description = description;
    }

    public RequestStatus() {}

    public long getStatusId() {
        return statusId;
    }

    public void setStatusId(long statusId) {
        this.statusId = statusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
