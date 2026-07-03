

import DTO.CustomerSignupDto;
import DTO.CustomerResponseDto;
import entity.*;
import exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.*;
import service.serviceImpl.CustomerServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    // ===== MOCK DEPENDENCIES =====

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // ===== CLASS UNDER TEST =====

    @InjectMocks
    private CustomerServiceImpl customerService;

    // =====================================================
    // 1. SIGNUP TEST
    // =====================================================

    @Test
    void signup_shouldCreateCustomerSuccessfully() {

        // 1. input DTO
        CustomerSignupDto dto = new CustomerSignupDto();
        dto.setEmail("test@mail.com");
        dto.setFirstName("Ali");
        dto.setLastName("Ahmadi");
        dto.setPassword("123");

        // 2. email does NOT exist
        when(customerRepository.findByEmail(dto.getEmail()))
                .thenReturn(null);

        // 3. encode password
        when(passwordEncoder.encode("123"))
                .thenReturn("encoded123");

        // 4. wallet save (mock void save)
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(i -> i.getArgument(0));

        // 5. customer save
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(i -> i.getArgument(0));

        // 6. call service
        CustomerResponseDto result = customerService.signup(dto);

        // 7. verify repository calls happened
        verify(customerRepository).save(any(Customer.class));
        verify(walletRepository).save(any(Wallet.class));

        // 8. result should NOT be null
        assertNotNull(result);
    }

    // =====================================================
    // 2. LOGIN SUCCESS
    // =====================================================

    @Test
    void login_shouldReturnCustomer_whenCredentialsAreCorrect() {

        Customer customer = new Customer();
        customer.setEmail("test@mail.com");
        customer.setPassword("encoded123");

        // user exists
        when(customerRepository.findByEmail("test@mail.com"))
                .thenReturn(customer);

        // password matches
        when(passwordEncoder.matches("123", "encoded123"))
                .thenReturn(true);

        Customer result = customerService.login("test@mail.com", "123");

        assertEquals("test@mail.com", result.getEmail());
    }

    // =====================================================
    // 3. LOGIN FAIL
    // =====================================================

    @Test
    void login_shouldThrowException_whenPasswordWrong() {

        Customer customer = new Customer();
        customer.setPassword("encoded123");

        when(customerRepository.findByEmail("test@mail.com"))
                .thenReturn(customer);

        when(passwordEncoder.matches("wrong", "encoded123"))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> customerService.login("test@mail.com", "wrong"));
    }

    // =====================================================
    // 4. WALLET CHARGE
    // =====================================================

    @Test
    void chargeWallet_shouldIncreaseBalance() {

        Wallet wallet = new Wallet();
        wallet.setBalance(100L);

        Customer customer = new Customer();
        customer.setWallet(wallet);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        customerService.chargeWallet(1L, 50L);

        assertEquals(150L, wallet.getBalance());
    }

    // =====================================================
    // 5. WALLET NOT FOUND CUSTOMER
    // =====================================================

    @Test
    void chargeWallet_shouldThrowException_whenCustomerNotFound() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> customerService.chargeWallet(1L, 50L));
    }
}