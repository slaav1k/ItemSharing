package rsreu.itemsharing.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "item_attributes")
public class ItemAttribute {

    @EmbeddedId
    private ItemAttributeId id;

    @Column(name = "value_text", length = 255)
    private String valueText;  // Для ENUM

    @Column(name = "value_number")
    private Double valueNumber; // Для NUMBER

    public ItemAttribute() {}

    public ItemAttribute(ItemAttributeId id, String valueText, Double valueNumber) {
        this.id = id;
        this.valueText = valueText;
        this.valueNumber = valueNumber;
    }

    public ItemAttributeId getId() {
        return id;
    }

    public void setId(ItemAttributeId id) {
        this.id = id;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public Double getValueNumber() {
        return valueNumber;
    }

    public void setValueNumber(Double valueNumber) {
        this.valueNumber = valueNumber;
    }
}
