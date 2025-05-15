package rsreu.itemsharing.dto;

import jakarta.validation.constraints.*;

public class SettingsFormDTO {

    @NotBlank(message = "ФИО обязательно")
    @Size(min = 2, max = 255, message = "ФИО должно быть от 2 до 255 символов")
    private String fullName;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "7\\d{10}", message = "Телефон должен быть в формате 7XXXXXXXXXX")
    private String phone;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    @Size(max = 50, message = "Email не должен превышать 50 символов")
    private String email;

    @NotBlank(message = "Город обязателен")
    private String city;

    @NotBlank(message = "Улица обязательна")
    @Size(max = 255, message = "Улица не должна превышать 255 символов")
    private String street;

    @NotBlank(message = "Номер дома обязателен")
    @Size(max = 50, message = "Номер дома не должен превышать 50 символов")
    private String houseNumber;

    @Size(max = 50, message = "Номер квартиры не должен превышать 50 символов")
    private String apartment;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 255, message = "Пароль должен быть от 6 до 255 символов")
    private String password;

    // Геттеры и сеттеры
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}