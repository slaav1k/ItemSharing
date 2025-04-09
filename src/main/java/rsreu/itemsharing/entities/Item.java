package rsreu.itemsharing.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "item")
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

    @Column(name = "weight", nullable = false)
    private double weight;

    @ManyToOne
    @JoinColumn(name = "color", nullable = false)
    private Color color;

    @ManyToOne
    @JoinColumn(name = "material", nullable = false)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "maker", nullable = false)
    private Maker maker;

    @ManyToOne
    @JoinColumn(name = "model", nullable = false)
    private Model model;

    @Column(name = "release_year", nullable = false)
    private long releaseYear;

    @OneToMany(mappedBy = "item")
    private List<ItemPhotoLink> itemPhotoLinks = new ArrayList<>();

    // Конструктор по умолчанию
    public Item() {
        this.itemId = UUID.randomUUID().toString();
    }

    // Полный конструктор
    public Item(String itemId, User owner, String name, Category category, String description, String address,
                boolean isAvailable, double weight, Color color, Material material, Maker maker, Model model,
                long releaseYear) {
        this.itemId = itemId;
        this.owner = owner;
        this.name = name;
        this.category = category;
        this.description = description;
        this.address = address;
        this.isAvailable = isAvailable;
        this.weight = weight;
        this.color = color;
        this.material = material;
        this.maker = maker;
        this.model = model;
        this.releaseYear = releaseYear;
    }

    // Геттеры и сеттеры
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
        this.isAvailable = available;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Maker getMaker() {
        return maker;
    }

    public void setMaker(Maker maker) {
        this.maker = maker;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
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