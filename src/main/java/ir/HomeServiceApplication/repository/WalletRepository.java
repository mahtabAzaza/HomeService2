package ir.HomeServiceApplication.repository;
import ir.HomeServiceApplication.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends
        JpaRepository<Wallet,Long> {

    Optional<Wallet> findByCustomer_Id(Long customerId);
}
