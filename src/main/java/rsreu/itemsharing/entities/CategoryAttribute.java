package rsreu.itemsharing.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "category_attribute")
public class CategoryAttribute {
    @EmbeddedId
    private CategoryAttributeId id;

    public CategoryAttribute() {}

    public CategoryAttribute(CategoryAttributeId id) {
        this.id = id;
    }

    public CategoryAttributeId getId() {
        return id;
    }

    public void setId(CategoryAttributeId id) {
        this.id = id;
    }
}
