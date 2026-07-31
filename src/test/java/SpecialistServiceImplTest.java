
import ir.HomeServiceApplication.DTO.SpecialistResponseDto;
import ir.HomeServiceApplication.DTO.SpecialistSignupDto;
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.ReviewRepository;
import ir.HomeServiceApplication.repository.SpecialistRepository;
import ir.HomeServiceApplication.repository.WalletRepository;
import ir.HomeServiceApplication.repository.WalletTransactionRepository;
import ir.HomeServiceApplication.service.VerificationService;
import ir.HomeServiceApplication.service.serviceImpl.SpecialistServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialistServiceImplTest {

    @Mock private SpecialistRepository specialistRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private WalletTransactionRepository walletTransactionRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private VerificationService verificationService;

    @InjectMocks
    private SpecialistServiceImpl specialistService;

    // =====================================================
    // SIGNUP
    // =====================================================

    // Creates a specialist, encodes the password, saves a new wallet, and returns the response DTO
    @Test
    void signup_shouldCreateSpecialistSuccessfully() {
        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setFirstName("Sara");
        dto.setLastName("Mohammadi");
        dto.setEmail("sara@mail.com");
        dto.setPassword("pass1234");
        dto.setProfileImage(null);

        when(specialistRepository.findByEmail("sara@mail.com")).thenReturn(null);
        when(passwordEncoder.encode("pass1234")).thenReturn("encoded");
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(specialistRepository.save(any(Specialist.class))).thenAnswer(i -> i.getArgument(0));

        SpecialistResponseDto result = specialistService.signup(dto);

        assertNotNull(result);
        verify(specialistRepository).save(any(Specialist.class));
        verify(walletRepository).save(any(Wallet.class));
    }

    // Throws and never calls save when the email is already registered to another specialist
    @Test
    void signup_shouldThrowException_whenEmailAlreadyInUse() {
        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setEmail("taken@mail.com");

        when(specialistRepository.findByEmail("taken@mail.com")).thenReturn(new Specialist());

        assertThrows(DuplicateEmailException.class, () -> specialistService.signup(dto));
        verify(specialistRepository, never()).save(any());
    }

    // Throws when the profile image byte array exceeds the 300 KB size limit
    @Test
    void signup_shouldThrowException_whenProfileImageTooLarge() {
        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setEmail("new@mail.com");
        dto.setProfileImage(new byte[301 * 1024]);

        when(specialistRepository.findByEmail("new@mail.com")).thenReturn(null);

        assertThrows(InvalidOperationException.class, () -> specialistService.signup(dto));
    }

    // =====================================================
    // FIND BY EMAIL
    // =====================================================

    // Returns the specialist whose email matches the given string
    @Test
    void findByEmail_shouldReturnSpecialist() {
        Specialist specialist = new Specialist();
        specialist.setEmail("spec@mail.com");

        when(specialistRepository.findByEmail("spec@mail.com")).thenReturn(specialist);

        Specialist result = specialistService.findByEmail("spec@mail.com");

        assertEquals("spec@mail.com", result.getEmail());
    }

    // =====================================================
    // LOGIN
    // =====================================================

    // Returns the specialist when the email/password match and the account is APPROVED
    @Test
    void login_shouldReturnSpecialist_whenCredentialsCorrect() {
        Specialist specialist = new Specialist();
        specialist.setEmail("spec@mail.com");
        specialist.setPassword("encoded");
        specialist.setStatus(SpecialistStatus.APPROVED);

        when(specialistRepository.findByEmail("spec@mail.com")).thenReturn(specialist);
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);

        Specialist result = specialistService.login("spec@mail.com", "raw");

        assertEquals("spec@mail.com", result.getEmail());
    }

    // Throws when the raw password does not match the stored encoded password
    @Test
    void login_shouldThrowException_whenPasswordWrong() {
        Specialist specialist = new Specialist();
        specialist.setPassword("encoded");
        specialist.setStatus(SpecialistStatus.APPROVED);

        when(specialistRepository.findByEmail("spec@mail.com")).thenReturn(specialist);
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> specialistService.login("spec@mail.com", "wrong"));
    }

    // Throws when no specialist exists with the given email
    @Test
    void login_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findByEmail("none@mail.com")).thenReturn(null);

        assertThrows(InvalidCredentialsException.class,
                () -> specialistService.login("none@mail.com", "any"));
    }

    // Throws even when credentials are correct if the specialist is still awaiting approval
    @Test
    void login_shouldThrowException_whenNotApproved() {
        Specialist specialist = new Specialist();
        specialist.setPassword("encoded");
        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);

        when(specialistRepository.findByEmail("spec@mail.com")).thenReturn(specialist);
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);

        assertThrows(NotApprovedException.class,
                () -> specialistService.login("spec@mail.com", "raw"));
    }

    // =====================================================
    // UPDATE PROFILE
    // =====================================================

    // Updates the specialist's fields and resets status back to WAITING_FOR_APPROVAL
    @Test
    void updateProfile_shouldUpdateSpecialistAndResetStatus() {
        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.APPROVED);

        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setEmail("new@mail.com");
        dto.setPassword("newpass1");
        dto.setProfileImage(null);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.existsBySpecialistIdAndOrderStatusIn(any(), any())).thenReturn(false);
        when(passwordEncoder.encode("newpass1")).thenReturn("encodedNew");

        specialistService.updateProfile(1L, dto);

        assertEquals("new@mail.com", specialist.getEmail());
        // after update, status must go back to WAITING_FOR_APPROVAL
        assertEquals(SpecialistStatus.WAITING_FOR_APPROVAL, specialist.getStatus());
    }

    // Throws when the given specialist ID does not exist in the repository
    @Test
    void updateProfile_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> specialistService.updateProfile(99L, new SpecialistSignupDto()));
    }

    // Throws when the specialist has an active or in-progress order that blocks profile changes
    @Test
    void updateProfile_shouldThrowException_whenHasOpenOrder() {
        Specialist specialist = new Specialist();

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.existsBySpecialistIdAndOrderStatusIn(any(), any())).thenReturn(true);

        assertThrows(InvalidOperationException.class,
                () -> specialistService.updateProfile(1L, new SpecialistSignupDto()));
    }

    // Throws when the new profile image byte array exceeds the 300 KB size limit
    @Test
    void updateProfile_shouldThrowException_whenProfileImageTooLarge() {
        Specialist specialist = new Specialist();

        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setProfileImage(new byte[301 * 1024]);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.existsBySpecialistIdAndOrderStatusIn(any(), any())).thenReturn(false);

        assertThrows(InvalidOperationException.class,
                () -> specialistService.updateProfile(1L, dto));
    }

    // Throws and leaves the specialist untouched when the new email is already taken by someone else
    @Test
    void updateProfile_shouldThrowException_whenNewEmailAlreadyInUse() {
        Specialist specialist = new Specialist();
        specialist.setEmail("old@mail.com");
        specialist.setStatus(SpecialistStatus.APPROVED);

        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setEmail("taken@mail.com");
        dto.setPassword("newpass1");

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.existsBySpecialistIdAndOrderStatusIn(any(), any())).thenReturn(false);
        when(specialistRepository.findByEmail("taken@mail.com")).thenReturn(new Specialist());

        assertThrows(DuplicateEmailException.class, () -> specialistService.updateProfile(1L, dto));
        assertEquals("old@mail.com", specialist.getEmail());
        assertEquals(SpecialistStatus.APPROVED, specialist.getStatus());
    }

    // A NEW (never-approved) specialist changing email stays NEW - there is no approved status to revoke
    @Test
    void updateProfile_shouldNotResetStatus_whenSpecialistWasNotApproved() {
        Specialist specialist = new Specialist();
        specialist.setEmail("old@mail.com");
        specialist.setStatus(SpecialistStatus.NEW);

        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setEmail("new@mail.com");
        dto.setPassword("newpass1");

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.existsBySpecialistIdAndOrderStatusIn(any(), any())).thenReturn(false);
        when(passwordEncoder.encode("newpass1")).thenReturn("encodedNew");

        specialistService.updateProfile(1L, dto);

        assertEquals(SpecialistStatus.NEW, specialist.getStatus());
        verify(verificationService).issueVerificationToken(specialist);
    }

    // Editing only first/last name does not revoke an already-approved specialist's status
    @Test
    void updateProfile_shouldNotResetStatus_whenOnlyNameChanged() {
        Specialist specialist = new Specialist();
        specialist.setEmail("same@mail.com");
        specialist.setStatus(SpecialistStatus.APPROVED);

        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setFirstName("NewFirst");
        dto.setLastName("NewLast");
        dto.setEmail("same@mail.com");

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.existsBySpecialistIdAndOrderStatusIn(any(), any())).thenReturn(false);

        specialistService.updateProfile(1L, dto);

        assertEquals("NewFirst", specialist.getFirstName());
        assertEquals(SpecialistStatus.APPROVED, specialist.getStatus());
        verify(verificationService, never()).issueVerificationToken(any());
    }

    // =====================================================
    // GET AVAILABLE ORDERS
    // =====================================================

    // Returns orders with WAITING_FOR_PROPOSAL status that match the specialist's registered services
    @Test
    void getAvailableOrders_shouldReturnOrders_whenApproved() {
        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.APPROVED);
        specialist.setServices(new ArrayList<>());

        List<Order> orders = List.of(new Order());

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findByServiceInAndOrderStatus(any(), eq(OrderStatus.WAITING_FOR_PROPOSAL)))
                .thenReturn(orders);

        List<Order> result = specialistService.getAvailableOrders(1L);

        assertEquals(1, result.size());
    }

    // Throws when the specialist is still waiting for approval and cannot see available orders
    @Test
    void getAvailableOrders_shouldThrowException_whenNotApproved() {
        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));

        assertThrows(NotApprovedException.class, () -> specialistService.getAvailableOrders(1L));
    }

    // Throws when the given specialist ID does not exist in the repository
    @Test
    void getAvailableOrders_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specialistService.getAvailableOrders(1L));
    }

    // =====================================================
    // GET WALLET BALANCE
    // =====================================================

    // Returns the balance from the specialist's associated wallet
    @Test
    void getWalletBalance_shouldReturnBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(500L);

        Specialist specialist = new Specialist();
        specialist.setWallet(wallet);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));

        Long balance = specialistService.getWalletBalance(1L);

        assertEquals(500L, balance);
    }

    // Throws when the given specialist ID does not exist in the repository
    @Test
    void getWalletBalance_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specialistService.getWalletBalance(1L));
    }

    // =====================================================
    // WITHDRAW
    // =====================================================

    // Subtracts the withdrawal amount from the specialist's wallet balance
    @Test
    void withdraw_shouldDecreaseBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(300L);

        Specialist specialist = new Specialist();
        specialist.setWallet(wallet);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));

        specialistService.withdraw(1L, 100L);

        assertEquals(200L, wallet.getBalance());
    }

    // Throws when the withdrawal amount exceeds the specialist's current wallet balance
    @Test
    void withdraw_shouldThrowException_whenInsufficientBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(50L);

        Specialist specialist = new Specialist();
        specialist.setWallet(wallet);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));

        assertThrows(InsufficientBalanceException.class, () -> specialistService.withdraw(1L, 200L));
    }

    // Throws when the given specialist ID does not exist in the repository
    @Test
    void withdraw_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specialistService.withdraw(1L, 100L));
    }
}
