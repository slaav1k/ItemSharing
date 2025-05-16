package rsreu.itemsharing.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import rsreu.itemsharing.dto.SettingsFormDTO;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.repositories.UserRepository;
import rsreu.itemsharing.security.CustomUserDetails;
import rsreu.itemsharing.services.CityService;

import jakarta.validation.Valid;
import java.security.Principal;

@Controller
public class SettingsController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CityService cityService;

    @Autowired
    public SettingsController(UserRepository userRepository, PasswordEncoder passwordEncoder, CityService cityService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cityService = cityService;
    }

    @GetMapping("/settings")
    public String showSettingsPage(Model model, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        System.out.println(currentUser);

        // Создаем DTO и заполняем его текущими данными
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

        model.addAttribute("settingsForm", settingsForm);
        model.addAttribute("cities", cityService.getCities());
        return "settings";
    }

    @PostMapping("/settings/update")
    public String updateSettings(
            @Valid @ModelAttribute("settingsForm") SettingsFormDTO settingsForm,
            BindingResult bindingResult,
            Model model,
            Principal principal) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

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

        if (bindingResult.hasErrors()) {
            model.addAttribute("cities", cityService.getCities());
            return "settings";
        }

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

        return "redirect:/settings";
    }

    @PostMapping("/settings/delete")
    @Transactional
    public String deleteAccount(HttpServletRequest request, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();
        userRepository.delete(currentUser);
        request.getSession().invalidate();
        return "redirect:/login?logout";
    }
}