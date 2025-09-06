//package backend.futurefinder.implementation.job;
//
//import backend.futurefinder.jpaentity.user.AcademicProfileJpaEntity;
//import backend.futurefinder.jparepository.job.AcademicProfileRepository;
//import backend.futurefinder.model.user.GraduationStatus;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import java.math.BigDecimal;
//
//@Component
//@RequiredArgsConstructor
//public class AcademicProfileAppender {
//    private final AcademicProfileRepository academicProfileRepository;
//
//    public AcademicProfileJpaEntity append(String userId, String schoolName, String major, String minor, BigDecimal currentGpa, BigDecimal maxGpa, GraduationStatus status) {
//        AcademicProfileJpaEntity academicProfile = new AcademicProfileJpaEntity(
//                schoolName, major, minor, maxGpa, currentGpa, userId, status
//        );
//        return academicProfileRepository.save(academicProfile);
//    }
//}