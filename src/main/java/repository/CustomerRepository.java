package repository;

import entity.Customer;

public interface CustomerRepository extends BaseRepository<Customer,Long> {
    Customer findByEmail(String email);


}
