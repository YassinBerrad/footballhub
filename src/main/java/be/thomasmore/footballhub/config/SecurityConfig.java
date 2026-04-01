package be.thomasmore.footballhub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${security.h2-console-needed:true}")
    private boolean h2ConsoleNeeded;

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(
                    "/",
                    "/about",
                    "/login",
                    "/register",

                    "/clublist",
                    "/clubdetails",
                    "/clubdetails/**",

                    "/playerlist",
                    "/playerdetails",
                    "/playerdetails/**",

                    "/stadiumlist",
                    "/stadiumdetails",
                    "/stadiumdetails/**",

                    "/css/**",
                    "/img/**"
            ).permitAll();

            if (h2ConsoleNeeded) {
                auth.requestMatchers("/h2-console/**").permitAll();
            }

            auth.requestMatchers(
                    "/clubcreate",
                    "/clubedit",
                    "/clubedit/**",
                    "/clubdelete/**",

                    "/playercreate",
                    "/playeredit",
                    "/playeredit/**",
                    "/playerdelete/**",

                    "/stadiumcreate",
                    "/stadiumedit",
                    "/stadiumedit/**",
                    "/stadiumdelete/**",

                    "/reservationlist"
            ).hasRole("ADMIN");

            auth.requestMatchers(
                    "/myreservations",
                    "/reservationcreate",
                    "/reservationdetails",
                    "/reservationdetails/**",
                    "/reservationdelete/**"
            ).authenticated();

            auth.anyRequest().authenticated();
        });

        http.formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        );

        if (h2ConsoleNeeded) {
            http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
            http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        }

        return http.build();
    }
}