package backend.futurefinder.jparepository.word;

import backend.futurefinder.jpaentity.economic.EconomicWordJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WordJpaRepository extends JpaRepository<EconomicWordJpaEntity, String> {
    Optional<EconomicWordJpaEntity> findByUserIdAndWordName(String userId, String wordName);
    List<EconomicWordJpaEntity> findAllByUserId(String userId, Pageable pageable);
    @Query("""
        SELECT e.wordName
        FROM EconomicWordJpaEntity e
        GROUP BY e.wordName
        ORDER BY COUNT(e) DESC, e.wordName ASC
    """)
    List<String> findPopularWordNames(Pageable pageable);

}
