package be.thomasmore.footballhub.services;

import be.thomasmore.footballhub.model.SiteUser;
import be.thomasmore.footballhub.repositories.SiteUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SiteUserRepository siteUserRepository;

    public CustomUserDetailsService(SiteUserRepository siteUserRepository) {
        this.siteUserRepository = siteUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SiteUser siteUser = siteUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Gebruiker niet gevonden: " + username));

        return User.builder()
                .username(siteUser.getUsername())
                .password(siteUser.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(siteUser.getRole())))
                .build();
    }
}