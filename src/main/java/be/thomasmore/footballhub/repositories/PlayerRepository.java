package be.thomasmore.footballhub.repositories;

import be.thomasmore.footballhub.model.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

    Iterable<Player> findByNameContainingIgnoreCase(String keyword);
}