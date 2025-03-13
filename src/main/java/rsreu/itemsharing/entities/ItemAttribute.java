package rsreu.itemsharing.entities;

public class ItemAttribute {
    private String item;
    private Long attribute;
    private String value;

    public ItemAttribute() {
    }

    public ItemAttribute(String item, Long attribute, String value) {
        this.item = item;
        this.attribute = attribute;
        this.value = value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
