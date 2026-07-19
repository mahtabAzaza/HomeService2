
import ir.HomeServiceApplication.DTO.CustomerResponseDto;
import ir.HomeServiceApplication.DTO.CustomerSignupDto;
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.exception.*;
import ir.HomeServiceApplication.repository.*;
import ir.HomeServiceApplication.service.SpecialistScoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ir.HomeServiceApplication.service.serviceImpl.CustomerServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private ProposalRepository proposalRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SpecialistScoreService ratingService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    // =====================================================
    // SIGNUP
    // =====================================================

    // Creates a customer, encodes the password, saves a new wallet, and returns the response DTO
    @Test
    void signup_shouldCreateCustomerSuccessfully() {
        CustomerSignupDto dto = new CustomerSignupDto();
        dto.setEmail("test@mail.com");
        dto.setFirstName("Ali");
        dto.setLastName("Ahmadi");
        dto.setPassword("pass1234");

        when(customerRepository.findByEmail(dto.getEmail())).thenReturn(null);
        when(passwordEncoder.encode("pass1234")).thenReturn("encoded");
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        CustomerResponseDto result = customerService.signup(dto);

        assertNotNull(result);
        verify(customerRepository).save(any(Customer.class));
        verify(walletRepository).save(any(Wallet.class));
    }

    // Throws and never calls save when the email is already registered to another customer
    @Test
    void signup_shouldThrowException_whenEmailAlreadyInUse() {
        CustomerSignupDto dto = new CustomerSignupDto();
        dto.setEmail("taken@mail.com");

        when(customerRepository.findByEmail("taken@mail.com")).thenReturn(new Customer());

        assertThrows(DuplicateEmailException.class, () -> customerService.signup(dto));
        verify(customerRepository, never()).save(any());
    }

    // =====================================================
    // LOGIN
    // =====================================================

    // Returns the customer when the email exists and the raw password matches the encoded one
    @Test
    void login_shouldReturnCustomer_whenCredentialsAreCorrect() {
        Customer customer = new Customer();
        customer.setEmail("test@mail.com");
        customer.setPassword("encoded");

        when(customerRepository.findByEmail("test@mail.com")).thenReturn(customer);
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);

        Customer result = customerService.login("test@mail.com", "raw");

        assertEquals("test@mail.com", result.getEmail());
    }

    // Throws when the raw password does not match the stored encoded password
    @Test
    void login_shouldThrowException_whenPasswordWrong() {
        Customer customer = new Customer();
        customer.setPassword("encoded");

        when(customerRepository.findByEmail("test@mail.com")).thenReturn(customer);
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> customerService.login("test@mail.com", "wrong"));
    }

    // Throws when no customer exists with the given email
    @Test
    void login_shouldThrowException_whenUserNotFound() {
        when(customerRepository.findByEmail("none@mail.com")).thenReturn(null);

        assertThrows(InvalidCredentialsException.class,
                () -> customerService.login("none@mail.com", "any"));
    }

    // =====================================================
    // FIND BY EMAIL
    // =====================================================

    // Returns the customer whose email matches the given string
    @Test
    void findByEmail_shouldReturnCustomer() {
        Customer customer = new Customer();
        customer.setEmail("test@mail.com");

        when(customerRepository.findByEmail("test@mail.com")).thenReturn(customer);

        Customer result = customerService.findByEmail("test@mail.com");

        assertEquals("test@mail.com", result.getEmail());
    }

    // =====================================================
    // UPDATE PROFILE
    // =====================================================

    // Updates the customer's email and encodes and sets the new password
    @Test
    void updateProfile_shouldUpdateCustomerFields() {
        Customer customer = new Customer();
        customer.setEmail("old@mail.com");

        CustomerSignupDto dto = new CustomerSignupDto();
        dto.setFirstName("Reza");
        dto.setLastName("Karimi");
        dto.setEmail("new@mail.com");
        dto.setPassword("newpass1");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("newpass1")).thenReturn("encodedNew");

        customerService.updateProfile(1L, dto);

        assertEquals("new@mail.com", customer.getEmail());
        assertEquals("encodedNew", customer.getPassword());
    }

    // Throws when the given customer ID does not exist in the repository
    @Test
    void updateProfile_shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> customerService.updateProfile(99L, new CustomerSignupDto()));
    }

    // =====================================================
    // GET SERVICES
    // =====================================================

    // Returns only root services (those with no parent service)
    @Test
    void getServices_shouldReturnRootServicesOnly() {
        Service root1 = new Service();
        Service root2 = new Service();
        Service child = new Service();
        child.setParentService(root1);

        when(serviceRepository.findAll()).thenReturn(List.of(root1, root2, child));

        List<Service> result = customerService.getServices();

        assertEquals(2, result.size());
        assertFalse(result.contains(child));
    }

    // =====================================================
    // PLACE ORDER
    // =====================================================

    // Creates and saves an order when the customer's offered price meets the service's base price
    @Test
    void placeOrder_shouldCreateOrder_whenPriceIsValid() {
        Customer customer = new Customer();
        Service service = new Service();
        service.setServiceBasePrice(100L);

        when(customerRepository.findByEmail("test@mail.com")).thenReturn(customer);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        customerService.placeOrder("test@mail.com", 1L, 150L, LocalDateTime.now(), "Tehran", "desc");

        verify(orderRepository).save(any(Order.class));
    }

    // Throws when the offered price is lower than the service's minimum base price
    @Test
    void placeOrder_shouldThrowException_whenPriceBelowBase() {
        Service service = new Service();
        service.setServiceBasePrice(200L);

        when(customerRepository.findByEmail("test@mail.com")).thenReturn(new Customer());
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        assertThrows(InvalidOperationException.class,
                () -> customerService.placeOrder("test@mail.com", 1L, 100L, LocalDateTime.now(), "Tehran", "desc"));
    }

    // Throws when no customer exists with the given email
    @Test
    void placeOrder_shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.findByEmail("missing@mail.com")).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> customerService.placeOrder("missing@mail.com", 1L, 100L, LocalDateTime.now(), "Tehran", "desc"));
    }

    // Throws when the given service ID does not exist in the repository
    @Test
    void placeOrder_shouldThrowException_whenServiceNotFound() {
        when(customerRepository.findByEmail("test@mail.com")).thenReturn(new Customer());
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> customerService.placeOrder("test@mail.com", 1L, 100L, LocalDateTime.now(), "Tehran", "desc"));
    }

    // =====================================================
    // GET MY ORDERS
    // =====================================================

    // Returns all orders that belong to the given customer
    @Test
    void getMyOrders_shouldReturnOrders() {
        Customer customer = new Customer();
        List<Order> orders = List.of(new Order(), new Order());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomer(customer)).thenReturn(orders);

        List<Order> result = customerService.getMyOrders(1L);

        assertEquals(2, result.size());
    }

    // Throws when the given customer ID does not exist in the repository
    @Test
    void getMyOrders_shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.getMyOrders(1L));
    }

    // =====================================================
    // GET PROPOSALS FOR ORDER
    // =====================================================

    // Returns proposals sorted by price ascending when sortBy is PRICE
    @Test
    void getProposalsForOrder_sortByPrice_shouldReturnProposals() {
        Order order = new Order();
        List<Proposal> proposals = List.of(new Proposal());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalRepository.findByOrderOrderByProposalPriceAsc(order)).thenReturn(proposals);

        List<Proposal> result = customerService.getProposalsForOrder(1L, ProposalSortByType.PRICE);

        assertEquals(1, result.size());
    }

    // Returns proposals sorted by specialist score descending when sortBy is SCORE
    @Test
    void getProposalsForOrder_sortByScore_shouldReturnSortedByScoreDesc() {
        Order order = new Order();

        Specialist specialistA = new Specialist();
        specialistA.setId(1L);
        Specialist specialistB = new Specialist();
        specialistB.setId(2L);

        Proposal lowScoreProposal = new Proposal();
        lowScoreProposal.setSpecialist(specialistA);

        Proposal highScoreProposal = new Proposal();
        highScoreProposal.setSpecialist(specialistB);

        List<Proposal> unsorted = new ArrayList<>(List.of(lowScoreProposal, highScoreProposal));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalRepository.findByOrder(order)).thenReturn(unsorted);
        when(reviewRepository.findAverageScoreBySpecialistId(1L)).thenReturn(2.0);
        when(reviewRepository.findAverageScoreBySpecialistId(2L)).thenReturn(4.5);

        List<Proposal> result = customerService.getProposalsForOrder(1L, ProposalSortByType.SCORE);

        assertEquals(highScoreProposal, result.get(0));
        assertEquals(lowScoreProposal, result.get(1));
    }

    // Throws when the given order ID does not exist in the repository
    @Test
    void getProposalsForOrder_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> customerService.getProposalsForOrder(99L, ProposalSortByType.PRICE));
    }

    // =====================================================
    // SELECT PROPOSAL
    // =====================================================

    // Assigns the proposal's specialist to the order, sets final price, and changes status to WAITING_FOR_SPECIALIST
    @Test
    void selectProposal_shouldSetSpecialistAndChangeStatus() {
        Specialist specialist = new Specialist();
        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_PROPOSAL);

        Proposal proposal = new Proposal();
        proposal.setSpecialist(specialist);
        proposal.setProposalPrice(500L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalRepository.findById(2L)).thenReturn(Optional.of(proposal));

        customerService.selectProposal(1L, 2L);

        assertEquals(OrderStatus.WAITING_FOR_SPECIALIST, order.getOrderStatus());
        assertEquals(specialist, order.getSpecialist());
        assertEquals(500L, order.getFinalPrice());
    }

    // Throws when the given order ID does not exist in the repository
    @Test
    void selectProposal_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.selectProposal(1L, 2L));
    }

    // =====================================================
    // MARK ORDER STARTED
    // =====================================================

    // Changes order status from WAITING_FOR_SPECIALIST to IN_PROGRESS when proposal start time has passed
    @Test
    void markOrderStarted_shouldChangeStatusToInProgress() {
        Proposal proposal = new Proposal();
        proposal.setStartDate(LocalDateTime.now().minusHours(1));

        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST);
        order.setSelectedProposal(proposal);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        customerService.markOrderStarted(1L);

        assertEquals(OrderStatus.IN_PROGRESS, order.getOrderStatus());
    }

    // Throws when the order is not in WAITING_FOR_SPECIALIST status
    @Test
    void markOrderStarted_shouldThrowException_whenWrongStatus() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_PROPOSAL);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> customerService.markOrderStarted(1L));
    }

    // Throws when the specialist has not arrived yet (proposal start time is in the future)
    @Test
    void markOrderStarted_shouldThrowException_whenTooEarly() {
        Proposal proposal = new Proposal();
        proposal.setStartDate(LocalDateTime.now().plusHours(2));

        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST);
        order.setSelectedProposal(proposal);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> customerService.markOrderStarted(1L));
    }

    // =====================================================
    // MARK ORDER DONE
    // =====================================================

    // Changes order status from IN_PROGRESS to DONE and calls late delivery penalty check
    @Test
    void markOrderDone_shouldChangeStatusToDone() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.IN_PROGRESS);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        customerService.markOrderDone(1L);

        assertEquals(OrderStatus.DONE, order.getOrderStatus());
        verify(ratingService).applyLateDeliveryPenalty(order);
    }

    // Throws when the order is not in IN_PROGRESS status
    @Test
    void markOrderDone_shouldThrowException_whenWrongStatus() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_PROPOSAL);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> customerService.markOrderDone(1L));
    }

    // =====================================================
    // PAY ORDER
    // =====================================================

    // Deducts the full price from the customer's wallet, credits 70% to the specialist, and marks the order PAID
    @Test
    void payOrder_shouldTransferFundsAndMarkPaid() {
        Wallet customerWallet = new Wallet();
        customerWallet.setBalance(1000L);

        Wallet specialistWallet = new Wallet();
        specialistWallet.setBalance(0L);

        Customer customer = new Customer();
        customer.setWallet(customerWallet);

        Specialist specialist = new Specialist();
        specialist.setWallet(specialistWallet);

        Order order = new Order();
        order.setOrderStatus(OrderStatus.DONE);
        order.setFinalPrice(300L);
        order.setCustomer(customer);
        order.setSpecialist(specialist);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        customerService.payOrder(1L);

        assertEquals(700L, customerWallet.getBalance());
        assertEquals(210L, specialistWallet.getBalance()); // 300 * 70% = 210
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
    }

    // Throws when the customer's wallet balance is less than the order's final price
    @Test
    void payOrder_shouldThrowException_whenInsufficientBalance() {
        Wallet customerWallet = new Wallet();
        customerWallet.setBalance(50L);

        Customer customer = new Customer();
        customer.setWallet(customerWallet);

        Specialist specialist = new Specialist();
        specialist.setWallet(new Wallet());

        Order order = new Order();
        order.setOrderStatus(OrderStatus.DONE);
        order.setFinalPrice(300L);
        order.setCustomer(customer);
        order.setSpecialist(specialist);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InsufficientBalanceException.class, () -> customerService.payOrder(1L));
    }

    // Throws when the order has not reached DONE status yet
    @Test
    void payOrder_shouldThrowException_whenOrderNotDone() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.IN_PROGRESS);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> customerService.payOrder(1L));
    }

    // =====================================================
    // SUBMIT REVIEW
    // =====================================================

    // Saves a review and applies the score to the specialist's rating
    @Test
    void submitReview_shouldSaveReviewAndUpdateScore() {
        Customer customer = new Customer();
        Specialist specialist = new Specialist();

        Order order = new Order();
        order.setOrderStatus(OrderStatus.DONE);
        order.setCustomer(customer);
        order.setSpecialist(specialist);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        customerService.submitReview(1L, 2L, 5, "Great work");

        verify(reviewRepository).save(any(Review.class));
        verify(ratingService).applyReviewScore(specialist, 5);
    }

    // Throws when the order is still IN_PROGRESS and has not been completed yet
    @Test
    void submitReview_shouldThrowException_whenOrderNotCompleted() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.IN_PROGRESS);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class,
                () -> customerService.submitReview(1L, 1L, 4, "ok"));
    }

    // =====================================================
    // GET WALLET BALANCE
    // =====================================================

    // Returns the balance from the customer's associated wallet
    @Test
    void getWalletBalance_shouldReturnBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(250L);

        Customer customer = new Customer();
        customer.setWallet(wallet);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Long balance = customerService.getWalletBalance(1L);

        assertEquals(250L, balance);
    }

    // Throws when the given customer ID does not exist in the repository
    @Test
    void getWalletBalance_shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.getWalletBalance(1L));
    }

    // =====================================================
    // CHARGE WALLET
    // =====================================================

    // Adds the given amount to the customer's wallet balance
    @Test
    void chargeWallet_shouldIncreaseBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(100L);

        Customer customer = new Customer();
        customer.setWallet(wallet);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.chargeWallet(1L, 50L);

        assertEquals(150L, wallet.getBalance());
    }

    // Throws when the given customer ID does not exist in the repository
    @Test
    void chargeWallet_shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.chargeWallet(1L, 50L));
    }
}
