package repository;

import entities.Customer;
import entities.Specialist;

public interface SpecialistRepository extends
        BaseRepository<Specialist,Integer> {
    Customer findByEmail(String email);

}
