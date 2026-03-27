package be.thomasmore.footballhub.repositories;

import be.thomasmore.footballhub.model.Stadium;
import org.springframework.data.repository.CrudRepository;

public interface StadiumRepository extends CrudRepository<Stadium, Integer> {

    Iterable<Stadium> findAllByOrderByIdAsc();
}