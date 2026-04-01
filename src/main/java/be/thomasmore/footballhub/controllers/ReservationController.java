package be.thomasmore.footballhub.controllers;

import be.thomasmore.footballhub.model.Reservation;
import be.thomasmore.footballhub.model.SiteUser;
import be.thomasmore.footballhub.model.Stadium;
import be.thomasmore.footballhub.repositories.ReservationRepository;
import be.thomasmore.footballhub.repositories.SiteUserRepository;
import be.thomasmore.footballhub.repositories.StadiumRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping({"/reservationdetails", "/reservationdetails/{id}"})
    public String reservationDetails(@PathVariable(required = false) Integer id,
                                     Authentication authentication,
                                     Model model) {

        if (id == null) {
            return "reservationdetails";
        }

        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isEmpty()) {
            return "reservationdetails";
        }

        Reservation reservation = optionalReservation.get();

        boolean isOwner = reservation.getSiteUser() != null
                && reservation.getSiteUser().getUsername().equals(authentication.getName());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return "redirect:/myreservations";
        }

        model.addAttribute("reservation", reservation);
        model.addAttribute("endHour", reservation.getStartHour() + reservation.getDurationHours());
        model.addAttribute("isAdmin", isAdmin);

        return "reservationdetails";
    }

    @GetMapping("/reservationcreate")
    public String reservationCreate(@RequestParam(required = false) Integer stadiumId,
                                    Authentication authentication,
                                    Model model) {

        model.addAttribute("stadiums", stadiumRepository.findAll());
        model.addAttribute("selectedStadiumId", stadiumId);
        model.addAttribute("currentUsername", authentication.getName());
        model.addAttribute("reservationDate", LocalDate.now().plusDays(1));
        model.addAttribute("startHour", 18);
        model.addAttribute("durationHours", 2);

        return "reservationcreate";
    }

    @PostMapping("/reservationcreate")
    public String reservationCreatePost(@RequestParam(required = false) Integer stadiumId,
                                        @RequestParam(required = false) LocalDate reservationDate,
                                        @RequestParam(required = false) Integer startHour,
                                        @RequestParam(required = false) Integer durationHours,
                                        Authentication authentication,
                                        Model model) {

        boolean hasErrors = false;

        if (stadiumId == null) {
            model.addAttribute("stadiumError", "Kies een stadium.");
            hasErrors = true;
        }

        if (reservationDate == null) {
            model.addAttribute("reservationDateError", "Datum is verplicht.");
            hasErrors = true;
        } else if (reservationDate.isBefore(LocalDate.now().plusDays(1))) {
            model.addAttribute("reservationDateError", "Reservaties moeten minstens vanaf morgen zijn.");
            hasErrors = true;
        }

        if (startHour == null) {
            model.addAttribute("startHourError", "Startuur is verplicht.");
            hasErrors = true;
        } else if (startHour < 8 || startHour > 22) {
            model.addAttribute("startHourError", "Startuur moet tussen 8 en 22 liggen.");
            hasErrors = true;
        }

        if (durationHours == null) {
            model.addAttribute("durationHoursError", "Duur is verplicht.");
            hasErrors = true;
        } else if (durationHours < 1 || durationHours > 8) {
            model.addAttribute("durationHoursError", "Duur moet tussen 1 en 8 uur liggen.");
            hasErrors = true;
        }

        if (startHour != null && durationHours != null) {
            int endHour = startHour + durationHours;

            if (endHour > 23) {
                model.addAttribute("durationHoursError", "Deze reservatie eindigt te laat. Kies een kortere duur of vroeger startuur.");
                hasErrors = true;
            }
        }

        if (hasErrors) {
            model.addAttribute("stadiums", stadiumRepository.findAll());
            model.addAttribute("selectedStadiumId", stadiumId);
            model.addAttribute("currentUsername", authentication.getName());
            model.addAttribute("reservationDate", reservationDate);
            model.addAttribute("startHour", startHour);
            model.addAttribute("durationHours", durationHours);
            return "reservationcreate";
        }

        Optional<SiteUser> optionalUser = siteUserRepository.findByUsername(authentication.getName());

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Stadium> optionalStadium = stadiumRepository.findById(stadiumId);

        if (optionalStadium.isEmpty()) {
            model.addAttribute("stadiums", stadiumRepository.findAll());
            model.addAttribute("selectedStadiumId", stadiumId);
            model.addAttribute("currentUsername", authentication.getName());
            model.addAttribute("reservationDate", reservationDate);
            model.addAttribute("startHour", startHour);
            model.addAttribute("durationHours", durationHours);
            model.addAttribute("stadiumError", "Ongeldig stadium gekozen.");
            return "reservationcreate";
        }

        Iterable<Reservation> existingReservations =
                reservationRepository.findByStadiumIdAndReservationDateOrderByStartHourAsc(stadiumId, reservationDate);

        int newStart = startHour;
        int newEnd = startHour + durationHours;

        for (Reservation existingReservation : existingReservations) {
            int existingStart = existingReservation.getStartHour();
            int existingEnd = existingReservation.getStartHour() + existingReservation.getDurationHours();

            boolean overlaps = newStart < existingEnd && newEnd > existingStart;

            if (overlaps) {
                model.addAttribute("stadiums", stadiumRepository.findAll());
                model.addAttribute("selectedStadiumId", stadiumId);
                model.addAttribute("currentUsername", authentication.getName());
                model.addAttribute("reservationDate", reservationDate);
                model.addAttribute("startHour", startHour);
                model.addAttribute("durationHours", durationHours);
                model.addAttribute("reservationConflictError",
                        "Dit stadium is al gereserveerd in dit tijdslot. Kies een ander uur of een andere datum.");
                return "reservationcreate";
            }
        }

        SiteUser siteUser = optionalUser.get();
        Stadium stadium = optionalStadium.get();

        Reservation reservation = new Reservation();
        reservation.setReservationDate(reservationDate);
        reservation.setStartHour(startHour);
        reservation.setDurationHours(durationHours);
        reservation.setSiteUser(siteUser);
        reservation.setStadium(stadium);
        reservation.setTotalPrice(stadium.getPricePerHour() * durationHours);

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