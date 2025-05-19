package rsreu.itemsharing.infrastructure;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;

@Document(indexName = "items")
public class ItemDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String itemId;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String name;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String description;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String address;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String color;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String material;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String maker;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String model;

//    @Field(type = FieldType.Text, analyzer = "russian")
    @Field(type = FieldType.Object)
    private Map<String, String> customAttributes;

    // Геттеры и сеттеры
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public ItemDocument() {}

    public ItemDocument(String itemId, String name, String description, String address, String color, String material, String maker, String model, Map<String, String> customAttributes) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.color = color;
        this.material = material;
        this.maker = maker;
        this.model = model;
        this.customAttributes = customAttributes;
    }
}