package ir.HomeServiceApplication.repository;

import ir.HomeServiceApplication.entity.PaymentSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentSession,Long> {
    Optional<PaymentSession> findByToken(String token) ;

}
