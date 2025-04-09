package rsreu.itemsharing.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "maker")
public class Maker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maker_id", nullable = false)
    private Long makerId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "country", nullable = false, length = 255)
    private String country;

    @Column(name = "year_of_foundation", nullable = false)
    private Long yearOfFoundation;

    public Maker() {}

    public Maker(String name, String country, Long yearOfFoundation) {
        this.name = name;
        this.country = country;
        this.yearOfFoundation = yearOfFoundation;
    }

    public Long getMakerId() {
        return makerId;
    }

    public void setMakerId(Long makerId) {
        this.makerId = makerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getYearOfFoundation() {
        return yearOfFoundation;
    }

    public void setYearOfFoundation(Long yearOfFoundation) {
        this.yearOfFoundation = yearOfFoundation;
    }
}