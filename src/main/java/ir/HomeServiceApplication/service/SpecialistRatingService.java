//package ir.HomeServiceApplication.service;
//
//import ir.HomeServiceApplication.entity.Specialist;
//import ir.HomeServiceApplication.entity.SpecialistStatus;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//
//@org.springframework.stereotype.Service
//@RequiredArgsConstructor
//@Transactional
//public class SpecialistRatingService {
//
//    public void applyLatePenalty(Specialist specialist, long hoursLate) {
//
//        specialist.setScore(specialist.getScore() - (int) hoursLate);
//
//        if (specialist.getScore() < 0) {
//            specialist.setStatus(SpecialistStatus.DEACTIVATED);
//        }
//    }
//
//    public void addReviewScore(Specialist specialist, int score) {
//
//        specialist.setScore(specialist.getScore() + score);
//    }
//}