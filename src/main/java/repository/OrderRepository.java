package repository;
import entity.Customer;
import entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends
        JpaRepository<Order,Long> {
}
