
import ir.HomeServiceApplication.entity.Customer;
import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ir.HomeServiceApplication.repository.CustomerRepository;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.SpecialistRepository;
import ir.HomeServiceApplication.service.serviceImpl.OrderServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private SpecialistRepository specialistRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    // =====================================================
    // GET ORDERS BY CUSTOMER
    // =====================================================

    // Returns all orders that belong to the given customer
    @Test
    void getOrdersByCustomer_shouldReturnOrders() {
        Customer customer = new Customer();
        List<Order> orders = List.of(new Order(), new Order());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomer(customer)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByCustomer(1L);

        assertEquals(2, result.size());
    }

    // Throws when the given customer ID does not exist in the repository
    @Test
    void getOrdersByCustomer_shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrdersByCustomer(99L));
    }

    // =====================================================
    // GET ORDERS BY SPECIALIST
    // =====================================================

    // Returns all orders that are assigned to the given specialist
    @Test
    void getOrdersBySpecialist_shouldReturnOrders() {
        Specialist specialist = new Specialist();
        List<Order> orders = List.of(new Order());

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findBySpecialist(specialist)).thenReturn(orders);

        List<Order> result = orderService.getOrdersBySpecialist(1L);

        assertEquals(1, result.size());
    }

    // Throws when the given specialist ID does not exist in the repository
    @Test
    void getOrdersBySpecialist_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrdersBySpecialist(99L));
    }
}
