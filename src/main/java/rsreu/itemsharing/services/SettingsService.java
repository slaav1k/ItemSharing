package rsreu.itemsharing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import rsreu.itemsharing.dto.SettingsFormDTO;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.repositories.UserRepository;

@Service
public class SettingsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public SettingsFormDTO prepareSettingsForm(User currentUser) {
        SettingsFormDTO settingsForm = new SettingsFormDTO();
        settingsForm.setFullName(currentUser.getFullName());
        settingsForm.setPhone(currentUser.getPhone());
        settingsForm.setEmail(currentUser.getEmail());

        // Разбираем адрес на части (предполагается формат: "Город, ул. Улица, д. Дом, кв. Квартира")
        String[] addressParts = currentUser.getAddress().split(", ");
        if (addressParts.length >= 3) {
            settingsForm.setCity(addressParts[0]);
            settingsForm.setStreet(addressParts[1].replace("ул. ", ""));
            settingsForm.setHouseNumber(addressParts[2].replace("д. ", ""));
            if (addressParts.length > 3) {
                settingsForm.setApartment(addressParts[3].replace("кв. ", ""));
            }
        }

        return settingsForm;
    }

    public void validateUserData(SettingsFormDTO settingsForm, User currentUser, BindingResult bindingResult) {
        // Проверка уникальности телефона
        if (userRepository.findByPhone(settingsForm.getPhone())
                .filter(user -> user.getPassportNum() != currentUser.getPassportNum())
                .isPresent()) {
            bindingResult.rejectValue("phone", "error.phone", "Этот номер телефона уже используется.");
        }

        // Проверка уникальности email
        if (userRepository.findByEmail(settingsForm.getEmail())
                .filter(user -> user.getPassportNum() != currentUser.getPassportNum())
                .isPresent()) {
            bindingResult.rejectValue("email", "error.email", "Этот email уже зарегистрирован.");
        }
    }

    public void updateUserSettings(SettingsFormDTO settingsForm, User currentUser) {
        // Обновление данных пользователя
        currentUser.setFullName(settingsForm.getFullName());
        currentUser.setPhone(settingsForm.getPhone());
        currentUser.setEmail(settingsForm.getEmail());

        // Формирование адреса
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(settingsForm.getCity())
                .append(", ул. ").append(settingsForm.getStreet())
                .append(", д. ").append(settingsForm.getHouseNumber());
        if (settingsForm.getApartment() != null && !settingsForm.getApartment().isEmpty()) {
            addressBuilder.append(", кв. ").append(settingsForm.getApartment());
        }
        currentUser.setAddress(addressBuilder.toString());

        // Обновление пароля
        currentUser.setPassword(passwordEncoder.encode(settingsForm.getPassword()));

        userRepository.save(currentUser);
    }

    @Transactional
    public void deleteUserAccount(User currentUser) {
        userRepository.delete(currentUser);
    }
}