package repository;

import entity.Customer;
import entity.Manager;
import entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends
        JpaRepository<Manager,Long> {
    Manager findByEmail(String email);
}
