package rsreu.itemsharing.entities;

import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Table(name = "users")
@Builder
public class User {

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

    public User() {
    }

    public User(long passportNum, String fullName, String phone, String email, String password, String address) {
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
