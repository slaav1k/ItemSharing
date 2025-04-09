package rsreu.itemsharing.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "color")
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "color_id", nullable = false)
    private Long colorId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    public Color() {}

    public Color(String name) {
        this.name = name;
    }

    public Long getColorId() {
        return colorId;
    }

    public void setColorId(Long colorId) {
        this.colorId = colorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}