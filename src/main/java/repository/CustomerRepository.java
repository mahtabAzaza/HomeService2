package repository;

import entities.Customer;

public interface CustomerRepository extends BaseRepository<Customer,Long> {
    Customer findByEmail(String email);


}
