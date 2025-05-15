package rsreu.itemsharing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.repositories.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String city, String street, String houseNumber, String apartment, User user, String phone) throws IllegalArgumentException {
        // Проверка уникальности passportNum
        if (userRepository.findById(user.getPassportNum()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким номером паспорта уже зарегистрирован.");
        }

        // Проверка уникальности email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже зарегистрирован.");
        }

        // Проверка уникальности phone
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new IllegalArgumentException("Телефон уже зарегистрирован.");
        }

        // Устанавливаем номер телефона
        user.setPhone(phone);

        // Склеивание адреса
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(city).append(", ул. ").append(street).append(", д. ").append(houseNumber);
        if (apartment != null && !apartment.isEmpty()) {
            addressBuilder.append(", кв. ").append(apartment);
        }
        user.setAddress(addressBuilder.toString());

        // Кодирование пароля и сохранение
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}