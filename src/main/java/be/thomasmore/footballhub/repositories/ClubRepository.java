package be.thomasmore.footballhub.repositories;

import be.thomasmore.footballhub.model.Club;
import org.springframework.data.repository.CrudRepository;

public interface ClubRepository extends CrudRepository<Club, Integer> {
}