package ir.HomeServiceApplication.repository;


import ir.HomeServiceApplication.entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistRepository extends
        JpaRepository <Specialist, Long> {
    Specialist findByEmail(String email);

}
