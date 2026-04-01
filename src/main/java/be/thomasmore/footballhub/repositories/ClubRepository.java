package be.thomasmore.footballhub.repositories;

import be.thomasmore.footballhub.model.Club;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ClubRepository extends CrudRepository<Club, Integer> {

    @Query("""
            select c
            from Club c
            where c.active = true
            and
                (:keyword is null or
                    lower(c.name) like lower(concat('%', :keyword, '%')) or
                    lower(c.city) like lower(concat('%', :keyword, '%')) or
                    lower(c.stadium) like lower(concat('%', :keyword, '%')))
            and
                (:minFoundedYear is null or c.foundedYear >= :minFoundedYear)
            order by c.id
            """)
    Iterable<Club> findByFilter(@Param("keyword") String keyword, @Param("minFoundedYear") Integer minFoundedYear);

    Iterable<Club> findAllByActiveTrueOrderByIdAsc();
}