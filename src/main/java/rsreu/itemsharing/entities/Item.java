package rsreu.itemsharing.entities;

public class Item {
    private String itemId;
    private long owner;
    private String name;
    private long category;
    private String description;
    private String address;
    private boolean isAvailable;
    private String sizes;
    private double weight;
    private String color;
    private String material;
    private String maker;
    private String model;
    private long releaseYear;

    public Item(String itemId, long owner, String name, long category, String description, String address, boolean isAvailable, String sizes, double weight, String color, String material, String maker, String model, long releaseYear) {
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

    public Item() {}

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
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
}
