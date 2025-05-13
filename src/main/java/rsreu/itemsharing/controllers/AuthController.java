package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.repositories.UserRepository;
import rsreu.itemsharing.services.AuthService;
import rsreu.itemsharing.services.CityService;

@Controller
@RequestMapping("/")
public class AuthController {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final CityService cityService;

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
        model.addAttribute("user", new User());
        model.addAttribute("cities", cityService.getCities());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @ModelAttribute("user") User user,
            @RequestParam("city") String city,
            @RequestParam("street") String street,
            @RequestParam("houseNumber") String houseNumber,
            @RequestParam(value = "apartment", required = false) String apartment,
            @RequestParam("phone") String phone,
            Model model) {

        try {
            authService.registerUser(city, street, houseNumber, apartment, user, phone);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Email")) {
                model.addAttribute("emailError", errorMessage);
            } else if (errorMessage.contains("Телефон уже зарегистрирован")) {
                model.addAttribute("phoneError", "Этот номер телефона уже используется.");
            } else if (errorMessage.contains("телефона")) {
                model.addAttribute("phoneError", "Неверный формат телефона. Используйте 7XXXXXXXXX.");
            }
            model.addAttribute("cities", cityService.getCities());
            return "register";
        }
    }
}