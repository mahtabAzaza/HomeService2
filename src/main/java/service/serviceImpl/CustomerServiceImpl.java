//package service.serviceImpl;
//
//import entities.Customer;
//import repository.*;
//import service.CustomerService;
//
//// @Service
//public class CustomerServiceImpl implements CustomerService {
//
//    // @Autowired
//    private final CustomerRepository customerRepository;
//    // @Autowired
//    private final OrderRepository orderRepository;
//    // @Autowired
//    private final ServiceRepository serviceRepository;
//    // @Autowired
//    private final SpecialistRepository specialistRepository;
//    // @Autowired
//    private final ReviewRepository reviewRepository;
//    public CustomerServiceImpl(CustomerRepository customerRepository,
//                               OrderRepository orderRepository,
//                               ServiceRepository serviceRepository,
//                               SpecialistRepository specialistRepository,
//                               ReviewRepository reviewRepository) {
//
//        this.customerRepository = customerRepository;
//        this.orderRepository = orderRepository;
//        this.serviceRepository = serviceRepository;
//        this.specialistRepository = specialistRepository;
//        this.reviewRepository = reviewRepository;
//    }
//
//    @Override
//    public Customer signUp(Customer customer) {
//
//        if (customerRepository.findByUsername(customer.getUsername()) != null) {
//            throw new DuplicateUsernameException("Username already exists");
//        }
//
//        return customerRepository.save(customer);
//    }
//
//    @Override
//    public Customer login(String username, String password) {
//
//        Customer customer =
//                customerRepository.findByUsername(username);
//
//        if (customer == null) {
//            throw new CustomerNotFoundException("Customer not found");
//        }
//
//        if (!customer.getPassword().equals(password)) {
//            throw new InvalidPasswordException("Wrong password");
//        }
//
//        return customer;
//    }
//
//    @Override
//    public void changeInfo(Customer customer) {
//
//        Customer existingCustomer =
//                customerRepository.findById(customer.getId());
//
//        if (existingCustomer == null) {
//            throw new CustomerNotFoundException("Customer not found");
//        }
//
//        customerRepository.update(customer);
//    }
//
//    @Override
//    public List<Service> seeServices() {
//        return serviceRepository.findAll();
//    }
//
//    @Override
//    public Order registerOrder(Order order) {
//
//        return orderRepository.save(order);
//    }
//
//    @Override
//    public void chooseSpecialist(Long orderId, Long specialistId) {
//
//        Order order =
//                orderRepository.findById(orderId);
//
//        if (order == null) {
//            throw new OrderNotFoundException("Order not found");
//        }
//
//        Specialist specialist =
//                specialistRepository.findById(specialistId);
//
//        if (specialist == null) {
//            throw new SpecialistNotFoundException("Specialist not found");
//        }
//
//        order.setSelectedSpecialist(specialist);
//
//        orderRepository.update(order);
//    }
//
//    @Override
//    public void chargeWallet(Long customerId, double amount) {
//
//        Customer customer =
//                customerRepository.findById(customerId);
//
//        if (customer == null) {
//            throw new CustomerNotFoundException("Customer not found");
//        }
//
//        customer.setWalletBalance(
//                customer.getWalletBalance() + amount
//        );
//
//        customerRepository.update(customer);
//    }
//
//    @Override
//    public void payOrder(Long customerId, Long orderId) {
//
//        Customer customer =
//                customerRepository.findById(customerId);
//
//        Order order =
//                orderRepository.findById(orderId);
//
//        if (customer == null) {
//            throw new CustomerNotFoundException("Customer not found");
//        }
//
//        if (order == null) {
//            throw new OrderNotFoundException("Order not found");
//        }
//
//        if (customer.getWalletBalance() < order.getPrice()) {
//            throw new InsufficientBalanceException(
//                    "Not enough balance"
//            );
//        }
//
//        customer.setWalletBalance(
//                customer.getWalletBalance() - order.getPrice()
//        );
//        customerRepository.update(customer);
//
//        // بعداً در Hibernate:
//        // order.setStatus(OrderStatus.PAID);
//
//        orderRepository.update(order);
//    }
//
//    @Override
//    public void review(Long customerId,
//                       Long orderId,
//                       int score,
//                       String comment) {
//
//        Review review = new Review();
//
//        review.setCustomer(
//                customerRepository.findById(customerId)
//        );
//
//        review.setOrder(
//                orderRepository.findById(orderId)
//        );
//
//        review.setScore(score);
//        review.setComment(comment);
//
//        reviewRepository.save(review);
//    }
//}