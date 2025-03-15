package rsreu.itemsharing.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item") // Соответствие таблице в БД
public class Item {

    @Id
    @Column(name = "item_id", nullable = false, length = 100)
    private String itemId;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category", nullable = false)
    private Category category;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @Column(name = "sizes", nullable = false, length = 255)
    private String sizes;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "color", nullable = false, length = 255)
    private String color;

    @Column(name = "material", nullable = false, length = 255)
    private String material;

    @Column(name = "maker", nullable = false, length = 255)
    private String maker;

    @Column(name = "model", nullable = false, length = 255)
    private String model;

    @Column(name = "release_year", nullable = false)
    private long releaseYear;

    @OneToMany(mappedBy = "item")
    private List<ItemPhotoLink> itemPhotoLinks = new ArrayList<>();

    public Item() {}

    public Item(String itemId, User owner, String name, Category category, String description, String address,
                boolean isAvailable, String sizes, double weight, String color, String material,
                String maker, String model, long releaseYear) {
        this.itemId = itemId;
        this.owner = owner;
        this.name = name;
        this.category = category;
        this.description = description;
        this.address = address;
        this.isAvailable = isAvailable;
        this.sizes = sizes;
        this.weight = weight;
        this.color = color;
        this.material = material;
        this.maker = maker;
        this.model = model;
        this.releaseYear = releaseYear;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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

    public long getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(long releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<ItemPhotoLink> getItemPhotoLinks() {
        return itemPhotoLinks;
    }

    public void setItemPhotoLinks(List<ItemPhotoLink> itemPhotoLinks) {
        this.itemPhotoLinks = itemPhotoLinks;
    }
}
