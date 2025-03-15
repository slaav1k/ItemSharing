package rsreu.itemsharing.laba2.entities;


public class UserLab2 {
    private long passportNum;
    private String fullName;
    private String phone;
    private String email;
    private String address;

    public UserLab2() {
    }

    public UserLab2(long passportNum, String fullName, String phone, String email, String address) {
        this.passportNum = passportNum;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
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
