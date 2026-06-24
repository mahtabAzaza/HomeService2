package repository;

import entity.Customer;
import entity.Specialist;

public interface ManagerRepository extends
        BaseRepository<Specialist,Long> {
    Customer findByEmail(String email);
}
