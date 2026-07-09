package ir.HomeServiceApplication.service.serviceImpl;

import ir.HomeServiceApplication.DTO.CustomerResponseDto;
import ir.HomeServiceApplication.DTO.CustomerSignupDto;

import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.exception.*;
import ir.HomeServiceApplication.mapper.CustomerMapper;

import ir.HomeServiceApplication.repository.*;
import ir.HomeServiceApplication.service.SpecialistRatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ir.HomeServiceApplication.service.CustomerService;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final SpecialistRatingService ratingService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               ServiceRepository serviceRepository,
                               OrderRepository orderRepository,
                               ProposalRepository proposalRepository,
                               WalletRepository walletRepository,
                               ReviewRepository reviewRepository,
                               PasswordEncoder passwordEncoder,
                               SpecialistRatingService ratingService) {
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.orderRepository = orderRepository;
        this.proposalRepository = proposalRepository;
        this.walletRepository = walletRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
        this.ratingService = ratingService;
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
//    public List<ir.HomeServiceApplication.entity.Service> getServices() {
//        return serviceRepository.findAll();
//    }

    public List<ir.HomeServiceApplication.entity.Service> getServices() {

        return serviceRepository.findAll()
                .stream()
                .filter(s -> s.getParentService() == null)
                .toList();
    }


    @Override
    public void placeOrder(String email, Long serviceId, Long priceOffer,
                           LocalDateTime startDateTime, String address, String description) {

        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) throw new NotFoundException("Customer not found");

        ir.HomeServiceApplication.entity.Service service = serviceRepository.findById(serviceId)
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
        order.setOrderSubmitDateTime(LocalDateTime.now());
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

    // مشاهده پیشنهادات
    @Override
    @Transactional(readOnly = true)
    public List<Proposal> getProposalsForOrder(Long orderId, ProposalSortByType sortBy) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (sortBy == ProposalSortByType.SCORE) {
            List<Proposal> proposals = new ArrayList<>(proposalRepository.findByOrder(order));
            proposals.sort((a, b) -> {
                Double scoreA = reviewRepository.findAverageScoreBySpecialistId(a.getSpecialist().getId());
                Double scoreB = reviewRepository.findAverageScoreBySpecialistId(b.getSpecialist().getId());
                if (scoreA == null) scoreA = 0.0;
                if (scoreB == null) scoreB = 0.0;
                return Double.compare(scoreB, scoreA);
            });
            return proposals;
        }

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
        // ذخیره پیشنهاد انتخاب شده ً
        order.setSelectedProposal(proposal);
        order.setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST);
    }

    @Override
    public void markOrderStarted(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.WAITING_FOR_SPECIALIST) {
            throw new InvalidOperationException("Order is not in correct state");
        }

        // گرفتن proposal انتخاب‌شده از  Order
        Proposal selectedProposal = order.getSelectedProposal();

        if (selectedProposal == null) {
            throw new NotFoundException("No proposal selected for this order");
        }

        // چک زمان شروع
        if (LocalDateTime.now().isBefore(selectedProposal.getStartDate())) {
            throw new InvalidOperationException("Too early to start order");
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

        // ثبت زمان واقعی پایان کار و بررسی تاخیر
        order.setCompletionTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.DONE);
        ratingService.applyLateDeliveryPenalty(order);
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
        Long price = order.getFinalPrice();
        if (customerWallet.getBalance() < price) {
            throw new InsufficientBalanceException("Not enough balance in wallet");
        }

        // ۷۰٪ مبلغ به کیف پول متخصص واریز می‌شود، ۳۰٪ کارمزد سیستم است
        customerWallet.setBalance(customerWallet.getBalance() - price);
        specialistWallet.setBalance(specialistWallet.getBalance() + (price * 70 / 100));
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

        ratingService.applyReviewScore(order.getSpecialist(), score);
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
