package be.thomasmore.footballhub.repositories;

import be.thomasmore.footballhub.model.SiteUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SiteUserRepository extends CrudRepository<SiteUser, Integer> {

    Optional<SiteUser> findByUsername(String username);

    boolean existsByUsername(String username);
}