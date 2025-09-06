//package backend.futurefinder.implementation.job;
//
//import backend.futurefinder.jpaentity.user.AcademicProfileJpaEntity;
//import backend.futurefinder.jparepository.job.AcademicProfileRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class AcademicProfileReader {
//    private final AcademicProfileRepository academicProfileRepository;
//
//    public Optional<AcademicProfileJpaEntity> findByUserId(String userId) {
//        return academicProfileRepository.findByUserId(userId);
//    }
//
//    public AcademicProfileJpaEntity readByUserId(String userId) {
//        return findByUserId(userId)
//                .orElseThrow(() -> new EntityNotFoundException("학업 정보가 등록되지 않았습니다."));
//    }
//}