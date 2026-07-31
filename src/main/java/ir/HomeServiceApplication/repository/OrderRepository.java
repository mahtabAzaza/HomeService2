package ir.HomeServiceApplication.repository;
import ir.HomeServiceApplication.DTO.OrderHistoryDto;
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.specification.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;


public interface OrderRepository extends
        JpaRepository<Order,Long> ,
        JpaSpecificationExecutor<Order>
{
    // customer views their own orders
    List<Order> findByCustomer(Customer customer);
    // specialist views orders assigned to them
    List<Order> findBySpecialist(Specialist specialist);
    // specialist sees open orders they can submit proposals for
// (orders in the services they work in)
    List<Order> findByServiceInAndOrderStatus(List<Service> services, OrderStatus status);



    boolean existsBySpecialistIdAndOrderStatusIn(Long specialistId, List<OrderStatus> statuses);

    // تاریخچه سفارشاتی که متخصص برای آن‌ها پیشنهاد داده
    List<Order> findByProposalsSpecialistId(Long specialistId);

    List<Order> findByCustomerIdAndOrderStatus(Long customerId, OrderStatus status);
}
