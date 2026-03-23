package be.thomasmore.footballhub.repositories;

import be.thomasmore.footballhub.model.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

    @Query("""
            select p
            from Player p
            where
                (:keyword is null or
                    lower(p.name) like lower(concat('%', :keyword, '%')) or
                    lower(p.position) like lower(concat('%', :keyword, '%')) or
                    lower(p.nationality) like lower(concat('%', :keyword, '%')))
            and
                (:clubId is null or p.club.id = :clubId)
            """)
    Iterable<Player> findByFilter(@Param("keyword") String keyword, @Param("clubId") Integer clubId);

    Iterable<Player> findAllByOrderByIdAsc();
}