package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rsreu.itemsharing.dto.RegisterFormDTO;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.repositories.UserRepository;
import rsreu.itemsharing.services.AuthService;
import rsreu.itemsharing.services.CityService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class AuthController {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CityService cityService;

    @Autowired
    public AuthController(final UserRepository userRepository, final AuthService authService, final CityService cityService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.cityService = cityService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterFormDTO());
        model.addAttribute("cities", cityService.getCities());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @Valid @ModelAttribute("registerForm") RegisterFormDTO registerForm,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("cities", cityService.getCities());
            return "register";
        }

        User user = new User();
        try {
            user.setPassportNum(Long.parseLong(registerForm.getPassportNum()));
        } catch (NumberFormatException e) {
            bindingResult.rejectValue("passportNum", "error.passportNum", "Номер паспорта должен быть числом.");
            model.addAttribute("cities", cityService.getCities());
            return "register";
        }
        user.setFullName(registerForm.getFullName());
        user.setEmail(registerForm.getEmail());
        user.setPassword(registerForm.getPassword());

        try {
            authService.registerUser(
                    registerForm.getCity(),
                    registerForm.getStreet(),
                    registerForm.getHouseNumber(),
                    registerForm.getApartment(),
                    user,
                    registerForm.getPhone()
            );
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("паспорта")) {
                model.addAttribute("passportError", errorMessage);
            } else if (errorMessage.contains("Email")) {
                model.addAttribute("emailError", errorMessage);
            } else if (errorMessage.contains("Телефон уже зарегистрирован")) {
                model.addAttribute("phoneError", "Этот номер телефона уже используется.");
            } else if (errorMessage.contains("телефона")) {
                model.addAttribute("phoneError", "Неверный формат телефона. Используйте 7XXXXXXXXXX.");
            }
            model.addAttribute("cities", cityService.getCities());
            return "register";
        }
    }
}