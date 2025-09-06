package backend.futurefinder.repository.jpa.auth;

import backend.futurefinder.jpaentity.auth.LoggedInJpaEntity;
import backend.futurefinder.model.token.RefreshToken;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.repository.auth.LoggedInRepository;
import backend.futurefinder.jparepository.auth.LoggedInJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LoggedInRepositoryImpl implements LoggedInRepository {

    private final LoggedInJpaRepository loggedInJpaRepository;



    @Override
    public void append(RefreshToken refreshToken, UserId userId) {
        LoggedInJpaEntity entity = LoggedInJpaEntity.generate(refreshToken, userId);
        loggedInJpaRepository.save(entity);
    }


    @Transactional
    @Override
    public void remove(String refreshToken) {
        loggedInJpaRepository.deleteByRefreshToken(refreshToken);
    }


    @Override
    public void update(RefreshToken refreshToken, RefreshToken preRefreshToken) {
        loggedInJpaRepository.findByRefreshToken(preRefreshToken.getToken())
                .ifPresent(entity -> {
                    entity.updateRefreshToken(refreshToken);
                    loggedInJpaRepository.save(entity);
                });
    }

    @Override
    public Optional<RefreshToken> read(String refreshToken, UserId userId) {
        return loggedInJpaRepository
                .findByRefreshTokenAndUserId(refreshToken, userId.getId())
                .map(LoggedInJpaEntity::toRefreshToken);
    }

}