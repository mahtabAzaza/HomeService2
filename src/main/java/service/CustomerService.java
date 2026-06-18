package service;

import entities.*;
import repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class CustomerService {

    private final BaseRepository<Customer, Long> customerRepository;
    private final BaseRepository<Service, Long> serviceRepository;
    private final BaseRepository<Order, Long> orderRepository;

    public CustomerService(BaseRepository<Customer, Long> customerRepository,
                           BaseRepository<Service, Long> serviceRepository,
                           BaseRepository<Order, Long> orderRepository) {
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.orderRepository = orderRepository;
    }

    /**
     *Sign up
     */
    public void signUp(Customer customer) {

        customer.setCustomerRegisterDate(LocalDateTime.now());
        customerRepository.save(customer);
    }

    /**
     * Log in
     */
    public Customer login(String email, String password) {

        return customerRepository.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email)
                        && c.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    /**
     * change customer info
     */
    public void changeInfo(Long customerId, Customer updated) {

        Customer customer = customerRepository.findById(customerId);

        if (Objects.isNull(customer)) {
            throw new RuntimeException("Customer not found");
        }

        customer.setName(updated.getName());
        customer.setEmail(updated.getEmail());
        customer.setPassword(updated.getPassword());
//    customerRepository.findByEmail(customer.getEmail());
//    if (existing != null){
//        throw new RuntimeException("Email already exists");
//    }
        customerRepository.update(customer);
    }

    /**
     * Show services and subservices
     */
    public List<Service> displayServices() {
        return serviceRepository.findAll();
    }

    /**
     *  sign an order
     */
    public void registerOrder(Long customerId, Order order) {

        Customer customer = customerRepository.findById(customerId);

        if (Objects.isNull(customer)) {
            throw new RuntimeException("Customer not found");
        }

        order.setCustomer(customer);
        order.setOrderStartDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.WAITING_FOR_PROPOSAL);

        orderRepository.save(order);
    }
}