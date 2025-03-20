package rsreu.itemsharing.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ItemAttributeId implements Serializable {

    @Column(name = "item_id")
    private String item;

    @Column(name = "attribute_id")
    private Long attribute;


    public ItemAttributeId() {}

    public ItemAttributeId(String item, Long attribute) {
        this.item = item;
        this.attribute = attribute;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Long getAttribute() {
        return attribute;
    }

    public void setAttribute(Long attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemAttributeId that = (ItemAttributeId) o;

        if (!item.equals(that.item)) return false;
        return attribute.equals(that.attribute);
    }

    @Override
    public int hashCode() {
        int result = item.hashCode();
        result = 31 * result + attribute.hashCode();
        return result;
    }
}
