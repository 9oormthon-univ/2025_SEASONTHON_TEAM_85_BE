package backend.futurefinder.jparepository.house;

import backend.futurefinder.jpaentity.house.ChatMessageJpaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageJpaEntity, Long> {
    List<ChatMessageJpaEntity> findByUserIdOrderByCreatedAtDesc(String userId, PageRequest pageRequest);
}