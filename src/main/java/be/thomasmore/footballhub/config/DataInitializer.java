package be.thomasmore.footballhub.config;

import be.thomasmore.footballhub.model.SiteUser;
import be.thomasmore.footballhub.repositories.SiteUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SiteUserRepository siteUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SiteUserRepository siteUserRepository, PasswordEncoder passwordEncoder) {
        this.siteUserRepository = siteUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!siteUserRepository.existsByUsername("admin")) {
            SiteUser admin = new SiteUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            siteUserRepository.save(admin);
        }
    }
}