package rsreu.itemsharing.infrastructure;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "items")
public class ItemDocument {
    @Id
    private String itemId;
    private String name;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String description;
    private String address;

    public ItemDocument() {}

    public ItemDocument(String itemId, String name, String description, String address) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.address = address;
    }
}

