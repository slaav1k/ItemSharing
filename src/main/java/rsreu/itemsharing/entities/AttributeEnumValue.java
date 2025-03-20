package rsreu.itemsharing.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "attribute_enum_value")
public class AttributeEnumValue {
    @EmbeddedId
    private AttributeEnumValueId id;

    public AttributeEnumValue() {}

    public AttributeEnumValue(AttributeEnumValueId id) {
        this.id = id;
    }

    public AttributeEnumValueId getId() {
        return id;
    }

    public void setId(AttributeEnumValueId id) {
        this.id = id;
    }
}
