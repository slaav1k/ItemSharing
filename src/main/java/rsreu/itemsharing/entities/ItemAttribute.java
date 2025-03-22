package rsreu.itemsharing.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "item_attributes")
public class ItemAttribute {

    @EmbeddedId
    private ItemAttributeId id;

    @Column(name = "value", length = 255)
    private String value;  // Для ENUM

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

    public Double getValueAsNumber() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
