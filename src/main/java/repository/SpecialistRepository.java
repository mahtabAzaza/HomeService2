package repository;


import entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistRepository extends
        JpaRepository <Specialist, Long> {
    Specialist findByEmail(String email);

}
