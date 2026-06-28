package repository;
import entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends
        JpaRepository<Order,Long> {
    // customer views their own orders
    List<Order> findByCustomer(Customer customer);
    // specialist views orders assigned to them
    List<Order> findBySpecialist(Specialist specialist);
    // specialist sees open orders they can submit proposals for
// (orders in the services they work in)
    List<Order> findByServiceInAndOrderStatus(List<Service> services, OrderStatus status);
}
