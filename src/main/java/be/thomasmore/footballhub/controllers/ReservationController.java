package be.thomasmore.footballhub.controllers;

import be.thomasmore.footballhub.model.Reservation;
import be.thomasmore.footballhub.model.SiteUser;
import be.thomasmore.footballhub.model.Stadium;
import be.thomasmore.footballhub.repositories.ReservationRepository;
import be.thomasmore.footballhub.repositories.SiteUserRepository;
import be.thomasmore.footballhub.repositories.StadiumRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final StadiumRepository stadiumRepository;
    private final SiteUserRepository siteUserRepository;

    public ReservationController(ReservationRepository reservationRepository,
                                 StadiumRepository stadiumRepository,
                                 SiteUserRepository siteUserRepository) {
        this.reservationRepository = reservationRepository;
        this.stadiumRepository = stadiumRepository;
        this.siteUserRepository = siteUserRepository;
    }

    @GetMapping("/reservationlist")
    public String reservationList(Model model) {
        model.addAttribute("reservations", reservationRepository.findAllByOrderByReservationDateAscStartHourAsc());
        return "reservationlist";
    }

    @GetMapping("/myreservations")
    public String myReservations(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("reservations", reservationRepository.findBySiteUserUsernameOrderByReservationDateAscStartHourAsc(username));
        return "myreservations";
    }

    @GetMapping("/reservationcreate")
    public String reservationCreate(@RequestParam(required = false) Integer stadiumId,
                                    Authentication authentication,
                                    Model model) {

        Reservation reservation = new Reservation();
        reservation.setReservationDate(LocalDate.now().plusDays(1));
        reservation.setStartHour(18);
        reservation.setDurationHours(2);

        if (stadiumId != null) {
            Optional<Stadium> optionalStadium = stadiumRepository.findById(stadiumId);
            optionalStadium.ifPresent(reservation::setStadium);
        }

        model.addAttribute("reservation", reservation);
        model.addAttribute("stadiums", stadiumRepository.findAll());
        model.addAttribute("currentUsername", authentication.getName());

        return "reservationcreate";
    }

    @PostMapping("/reservationcreate")
    public String reservationCreatePost(@Valid Reservation reservation,
                                        BindingResult bindingResult,
                                        Authentication authentication,
                                        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("stadiums", stadiumRepository.findAll());
            model.addAttribute("currentUsername", authentication.getName());
            return "reservationcreate";
        }

        Optional<SiteUser> optionalUser = siteUserRepository.findByUsername(authentication.getName());

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Stadium> optionalStadium = stadiumRepository.findById(reservation.getStadium().getId());

        if (optionalStadium.isEmpty()) {
            model.addAttribute("stadiums", stadiumRepository.findAll());
            model.addAttribute("currentUsername", authentication.getName());
            model.addAttribute("stadiumError", "Ongeldig stadium gekozen.");
            return "reservationcreate";
        }

        SiteUser siteUser = optionalUser.get();
        Stadium stadium = optionalStadium.get();

        reservation.setSiteUser(siteUser);
        reservation.setStadium(stadium);
        reservation.setTotalPrice(stadium.getPricePerHour() * reservation.getDurationHours());

        reservationRepository.save(reservation);

        return "redirect:/myreservations";
    }

    @PostMapping("/reservationdelete/{id}")
    public String reservationDelete(@PathVariable Integer id, Authentication authentication) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();

            boolean isOwner = reservation.getSiteUser() != null
                    && reservation.getSiteUser().getUsername().equals(authentication.getName());

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            if (isOwner || isAdmin) {
                reservationRepository.deleteById(id);
            }
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return "redirect:/reservationlist";
        }

        return "redirect:/myreservations";
    }
}