package repository;

import entities.Customer;
import entities.Specialist;

public interface ManagerRepository extends
        BaseRepository<Specialist,Long> {
    Customer findByEmail(String email);
}
