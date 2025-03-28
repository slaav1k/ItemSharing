package rsreu.itemsharing.laba3.entities;

import jakarta.persistence.*;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.Review;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User3 {

    @Id
    @Column(name = "passport_num", nullable = false)
    private long passportNum;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone", nullable = false, length = 11)
    private String phone;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "address", nullable = false)
    private String address;

    public User3() {
    }

    public User3(long passportNum, String fullName, String phone, String email, String password, String address) {
        this.passportNum = passportNum;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.address = address;
    }

    @Override
    public String toString() {
        return fullName;
    }

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "reviewed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsReceived = new ArrayList<>();

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Review> getReviewsReceived() {
        return reviewsReceived;
    }

    public void setReviewsReceived(List<Review> reviewsReceived) {
        this.reviewsReceived = reviewsReceived;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getPassportNum() {
        return passportNum;
    }

    public void setPassportNum(long passportNum) {
        this.passportNum = passportNum;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

