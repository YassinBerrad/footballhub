package be.thomasmore.footballhub.repositories;

import be.thomasmore.footballhub.model.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

    @Query("""
            select p
            from Player p
            left join fetch p.club
            where p.active = true
            and
                (:keyword is null or
                    lower(p.name) like lower(concat('%', :keyword, '%')) or
                    lower(p.position) like lower(concat('%', :keyword, '%')) or
                    lower(p.nationality) like lower(concat('%', :keyword, '%')))
            and
                (:position is null or lower(p.position) = lower(:position))
            and
                (:minAge is null or p.age >= :minAge)
            and
                (:maxAge is null or p.age <= :maxAge)
            order by p.id
            """)
    Iterable<Player> findByFilterWithoutClubs(@Param("keyword") String keyword,
                                              @Param("position") String position,
                                              @Param("minAge") Integer minAge,
                                              @Param("maxAge") Integer maxAge);

    @Query("""
            select p
            from Player p
            left join fetch p.club
            where p.active = true
            and
                (:keyword is null or
                    lower(p.name) like lower(concat('%', :keyword, '%')) or
                    lower(p.position) like lower(concat('%', :keyword, '%')) or
                    lower(p.nationality) like lower(concat('%', :keyword, '%')))
            and
                p.club.id in :clubIds
            and
                (:position is null or lower(p.position) = lower(:position))
            and
                (:minAge is null or p.age >= :minAge)
            and
                (:maxAge is null or p.age <= :maxAge)
            order by p.id
            """)
    Iterable<Player> findByFilterWithClubs(@Param("keyword") String keyword,
                                           @Param("clubIds") List<Integer> clubIds,
                                           @Param("position") String position,
                                           @Param("minAge") Integer minAge,
                                           @Param("maxAge") Integer maxAge);

    @Query("""
            select p
            from Player p
            left join fetch p.club
            where p.active = true
            order by p.id
            """)
    Iterable<Player> findAllByActiveTrueOrderByIdAsc();
}