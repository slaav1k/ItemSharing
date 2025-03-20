package rsreu.itemsharing.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AttributeEnumValueId implements Serializable {
    private Long attributeId;
    private String value;

    public AttributeEnumValueId() {}

    public AttributeEnumValueId(Long attributeId, String value) {
        this.attributeId = attributeId;
        this.value = value;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeEnumValueId that = (AttributeEnumValueId) o;
        return Objects.equals(attributeId, that.attributeId) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeId, value);
    }
}
