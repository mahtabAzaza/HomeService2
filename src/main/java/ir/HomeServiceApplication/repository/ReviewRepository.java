package ir.HomeServiceApplication.repository;

import ir.HomeServiceApplication.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {


    // امتیاز میانگین متخصص
    @Query("SELECT AVG(r.score) FROM Review r WHERE r.specialist.id = :specialistId")
    Double findAverageScoreBySpecialistId(@Param("specialistId") Long specialistId);

    // امتیاز یک سفارش خاص (بدون متن نظر)
    @Query("SELECT r.score FROM Review r WHERE r.order.id = :orderId AND r.specialist.id = :specialistId")
    Integer findScoreByOrderIdAndSpecialistId(@Param("orderId") Long orderId, @Param("specialistId") Long specialistId);
}
