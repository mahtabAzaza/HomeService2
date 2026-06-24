package repository;

import entity.Customer;
import entity.Specialist;

public interface SpecialistRepository extends
        BaseRepository<Specialist,Integer> {
    Customer findByEmail(String email);

}
