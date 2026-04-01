package be.thomasmore.footballhub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Datum is verplicht.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;

    @NotNull(message = "Startuur is verplicht.")
    @Min(value = 8, message = "Startuur moet minstens 8 zijn.")
    @Max(value = 22, message = "Startuur mag maximum 22 zijn.")
    private Integer startHour;

    @NotNull(message = "Duur is verplicht.")
    @Min(value = 1, message = "Duur moet minstens 1 uur zijn.")
    @Max(value = 8, message = "Duur mag maximum 8 uur zijn.")
    private Integer durationHours;

    private Integer totalPrice;

    private boolean active = true;

    @NotNull(message = "Gebruiker is verplicht.")
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser siteUser;

    @NotNull(message = "Stadium is verplicht.")
    @ManyToOne(fetch = FetchType.LAZY)
    private Stadium stadium;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public SiteUser getSiteUser() {
        return siteUser;
    }

    public void setSiteUser(SiteUser siteUser) {
        this.siteUser = siteUser;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }
}