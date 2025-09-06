//package backend.futurefinder.facade.job;
//
//import backend.futurefinder.jpaentity.user.AcademicProfileJpaEntity;
//import backend.futurefinder.model.user.GraduationStatus;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import java.math.BigDecimal;
//
//@Component
//@RequiredArgsConstructor
//public class AcademicFacade {
//    private final AcademicService academicService;
//
//    public AcademicProfileJpaEntity createOrUpdateAcademicInfo(Long userId, String schoolName, String major, String minor, BigDecimal currentGpa, BigDecimal maxGpa, GraduationStatus status) {
//        return academicService.createOrUpdateAcademicInfo(userId, schoolName, major, minor, currentGpa, maxGpa, status);
//    }
//
//    public AcademicProfileJpaEntity getAcademicInfo(Long userId) {
//        return academicService.getAcademicInfo(userId);
//    }
//}