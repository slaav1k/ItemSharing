package rsreu.itemsharing.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import rsreu.itemsharing.dto.SettingsFormDTO;
import rsreu.itemsharing.entities.User;
import rsreu.itemsharing.security.CustomUserDetails;
import rsreu.itemsharing.services.CityService;
import rsreu.itemsharing.services.SettingsService;

import jakarta.validation.Valid;
import java.security.Principal;

@Controller
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CityService cityService;

    @GetMapping("/settings")
    public String showSettingsPage(Model model, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        System.out.println(currentUser);

        SettingsFormDTO settingsForm = settingsService.prepareSettingsForm(currentUser);

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

        settingsService.validateUserData(settingsForm, currentUser, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("cities", cityService.getCities());
            return "settings";
        }

        settingsService.updateUserSettings(settingsForm, currentUser);

        return "redirect:/settings";
    }

    @PostMapping("/settings/delete")
    public String deleteAccount(HttpServletRequest request, Principal principal) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = customUserDetails.getUser();

        settingsService.deleteUserAccount(currentUser);

        request.getSession().invalidate();
        return "redirect:/login?logout";
    }
}