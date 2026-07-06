package ir.HomeServiceApplication.service.serviceImpl;

import ir.HomeServiceApplication.entity.Customer;
import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ir.HomeServiceApplication.repository.CustomerRepository;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.SpecialistRepository;
import ir.HomeServiceApplication.service.OrderService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final SpecialistRepository specialistRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            SpecialistRepository specialistRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.specialistRepository = specialistRepository;
    }

    @Override
    public List<Order> getOrdersByCustomer(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        return orderRepository.findByCustomer(customer);
    }

    @Override
    public List<Order> getOrdersBySpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        return orderRepository.findBySpecialist(specialist);
    }
}
