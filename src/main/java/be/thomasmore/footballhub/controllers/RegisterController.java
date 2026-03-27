package be.thomasmore.footballhub.controllers;

import be.thomasmore.footballhub.model.SiteUser;
import be.thomasmore.footballhub.repositories.SiteUserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final SiteUserRepository siteUserRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(SiteUserRepository siteUserRepository, PasswordEncoder passwordEncoder) {
        this.siteUserRepository = siteUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("siteUser", new SiteUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid SiteUser siteUser,
                               BindingResult bindingResult,
                               @RequestParam String confirmPassword,
                               Model model) {

        if (siteUser.getUsername() != null && siteUserRepository.existsByUsername(siteUser.getUsername())) {
            model.addAttribute("usernameError", "Deze gebruikersnaam bestaat al.");
        }

        if (confirmPassword == null || confirmPassword.isBlank()) {
            model.addAttribute("confirmPasswordError", "Bevestig je wachtwoord.");
        }

        if (siteUser.getPassword() != null
                && confirmPassword != null
                && !siteUser.getPassword().equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "Wachtwoorden komen niet overeen.");
        }

        if (bindingResult.hasErrors()
                || model.containsAttribute("usernameError")
                || model.containsAttribute("confirmPasswordError")) {
            return "register";
        }

        siteUser.setPassword(passwordEncoder.encode(siteUser.getPassword()));
        siteUser.setRole("ROLE_USER");

        siteUserRepository.save(siteUser);

        return "redirect:/login?registered";
    }
}