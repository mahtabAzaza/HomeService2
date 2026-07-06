package ir.HomeServiceApplication.repository;

import ir.HomeServiceApplication.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends
        JpaRepository<Manager,Long> {
    Manager findByEmail(String email);
}
