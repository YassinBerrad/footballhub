package be.thomasmore.footballhub.repositories;

import be.thomasmore.footballhub.model.Reservation;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

    Iterable<Reservation> findAllByOrderByReservationDateAscStartHourAsc();

    Iterable<Reservation> findBySiteUserUsernameOrderByReservationDateAscStartHourAsc(String username);

    Iterable<Reservation> findByStadiumIdAndReservationDateOrderByStartHourAsc(Integer stadiumId, LocalDate reservationDate);
}