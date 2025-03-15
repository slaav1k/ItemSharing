package rsreu.itemsharing.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "item_attributes")
public class ItemAttribute {

    @EmbeddedId
    private ItemAttributeId id;

    @Column(name = "value", nullable = false, length = 255)
    private String value;

    public ItemAttribute() {}

    public ItemAttribute(ItemAttributeId id, String value) {
        this.id = id;
        this.value = value;
    }

    public ItemAttributeId getId() {
        return id;
    }

    public void setId(ItemAttributeId id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
