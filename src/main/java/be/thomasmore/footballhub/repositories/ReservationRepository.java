package be.thomasmore.footballhub.repositories;

import be.thomasmore.footballhub.model.Reservation;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

    Iterable<Reservation> findAllByActiveTrueOrderByReservationDateAscStartHourAsc();

    Iterable<Reservation> findBySiteUserUsernameAndActiveTrueOrderByReservationDateAscStartHourAsc(String username);

    Iterable<Reservation> findByStadiumIdAndReservationDateAndActiveTrueOrderByStartHourAsc(Integer stadiumId, LocalDate reservationDate);

    long countByStadiumIdAndActiveTrue(Integer stadiumId);
}