package service.serviceImpl;

import DTO.CustomerResponseDto;
import DTO.CustomerSignupDto;
import entity.*;
import mapper.CustomerMapper;
import exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;
import service.CustomerService;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final OrderRepository orderRepository;
    private final ProposalRepository proposalRepository;
    private final WalletRepository walletRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               ServiceRepository serviceRepository,
                               OrderRepository orderRepository,
                               ProposalRepository proposalRepository,
                               WalletRepository walletRepository,
                               ReviewRepository reviewRepository,
                               PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.orderRepository = orderRepository;
        this.proposalRepository = proposalRepository;
        this.walletRepository = walletRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerResponseDto signup(CustomerSignupDto dto) {

        if (customerRepository.findByEmail(dto.getEmail()) != null) {
            throw new DuplicateEmailException("Email already in use");
        }

        Wallet wallet = new Wallet();
        wallet.setBalance(0L);
        wallet.setOwnerName(dto.getFirstName() + " " + dto.getLastName());
        walletRepository.save(wallet);

        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customer.setProfilePicture(dto.getProfilePicture());
        customer.setRegistrationDate(LocalDateTime.now());
        customer.setRole(Role.CUSTOMER);
        customer.setWallet(wallet);

        customerRepository.save(customer);
        return CustomerMapper.toDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer login(String email, String password) {

        Customer customer = customerRepository.findByEmail(email);

        if (customer == null || !passwordEncoder.matches(password, customer.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return customer;
    }

    @Override
    public void updateProfile(Long customerId, CustomerSignupDto dto) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<entity.Service> getServices() {
        return serviceRepository.findAll();
    }
// pagination
    // print child-parent
    @Override
    public void placeOrder(Long customerId, Long serviceId, Long priceOffer,
                           LocalDateTime startDateTime, String address, String description) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        if (priceOffer < service.getServiceBasePrice()) {
            throw new InvalidOperationException("Price must be >= base price of the service");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setService(service);
        order.setPriceOffer(priceOffer);
        order.setOrderStartDateTime(startDateTime);
        order.setAddress(address);
        order.setOrderDescription(description);
        order.setOrderStatus(OrderStatus.WAITING_FOR_PROPOSAL);

        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getMyOrders(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        return orderRepository.findByCustomer(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proposal> getProposalsForOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        return proposalRepository.findByOrderOrderByProposalPriceAsc(order);
    }

    @Override
    public void selectProposal(Long orderId, Long proposalId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new NotFoundException("Proposal not found"));

        if (order.getOrderStatus() != OrderStatus.WAITING_FOR_PROPOSAL) {
            throw new InvalidOperationException("no proposal yet");
        }

        order.setSpecialist(proposal.getSpecialist());
        order.setFinalPrice(proposal.getProposalPrice());
        order.setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST);
    }

    // شروع کار
    @Override
    public void markOrderStarted(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.WAITING_FOR_SPECIALIST) {
            throw new InvalidOperationException("Order is not in the correct state to start");
        }

        order.setOrderStatus(OrderStatus.IN_PROGRESS);
    }


    // پایان کار
    @Override
    public void markOrderDone(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.IN_PROGRESS) {
            throw new InvalidOperationException("Order has not been started yet");
        }

        order.setOrderStatus(OrderStatus.DONE);
    }


    @Override
    public void payOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.DONE) {
            throw new InvalidOperationException("Order must be completed before payment");
        }

        Wallet customerWallet = order.getCustomer().getWallet();
        Wallet specialistWallet = order.getSpecialist().getWallet();

        if (customerWallet.getBalance() < order.getFinalPrice()) {
            throw new InsufficientBalanceException("Not enough balance in wallet");
        }

        customerWallet.setBalance(customerWallet.getBalance() - order.getFinalPrice());
        specialistWallet.setBalance(specialistWallet.getBalance() + order.getFinalPrice());

        order.setOrderStatus(OrderStatus.PAID);
    }

    @Override
    public void submitReview(Long customerId, Long orderId, int score, String statement) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.DONE && order.getOrderStatus() != OrderStatus.PAID) {
            throw new InvalidOperationException("Cannot review an order that is not completed");
        }

        Review review = new Review();
        review.setCustomer(customer);
        review.setSpecialist(order.getSpecialist());
        review.setOrder(order);
        review.setScore(score);
        review.setStatement(statement);

        reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getWalletBalance(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        return customer.getWallet().getBalance();
    }

    @Override
    @Transactional
    public void chargeWallet(Long customerId, Long amount) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Wallet wallet = customer.getWallet();
        wallet.setBalance(wallet.getBalance() + amount);
    }
}
