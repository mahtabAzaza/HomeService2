
import DTO.SpecialistResponseDto;
import DTO.SpecialistSignupDto;
import entity.*;
import exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.OrderRepository;
import repository.SpecialistRepository;
import repository.WalletRepository;
import service.serviceImpl.SpecialistServiceImpl;

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
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SpecialistServiceImpl specialistService;

    // =====================================================
    // SIGNUP
    // =====================================================

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

    @Test
    void signup_shouldThrowException_whenEmailAlreadyInUse() {
        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setEmail("taken@mail.com");

        when(specialistRepository.findByEmail("taken@mail.com")).thenReturn(new Specialist());

        assertThrows(DuplicateEmailException.class, () -> specialistService.signup(dto));
        verify(specialistRepository, never()).save(any());
    }

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

    @Test
    void login_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findByEmail("none@mail.com")).thenReturn(null);

        assertThrows(InvalidCredentialsException.class,
                () -> specialistService.login("none@mail.com", "any"));
    }

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

    @Test
    void updateProfile_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> specialistService.updateProfile(99L, new SpecialistSignupDto()));
    }

    @Test
    void updateProfile_shouldThrowException_whenHasOpenOrder() {
        Specialist specialist = new Specialist();

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.existsBySpecialistIdAndOrderStatusIn(any(), any())).thenReturn(true);

        assertThrows(InvalidOperationException.class,
                () -> specialistService.updateProfile(1L, new SpecialistSignupDto()));
    }

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

    // =====================================================
    // GET AVAILABLE ORDERS
    // =====================================================

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

    @Test
    void getAvailableOrders_shouldThrowException_whenNotApproved() {
        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));

        assertThrows(NotApprovedException.class, () -> specialistService.getAvailableOrders(1L));
    }

    @Test
    void getAvailableOrders_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specialistService.getAvailableOrders(1L));
    }

    // =====================================================
    // GET WALLET BALANCE
    // =====================================================

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

    @Test
    void getWalletBalance_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specialistService.getWalletBalance(1L));
    }

    // =====================================================
    // WITHDRAW
    // =====================================================

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

    @Test
    void withdraw_shouldThrowException_whenInsufficientBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(50L);

        Specialist specialist = new Specialist();
        specialist.setWallet(wallet);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));

        assertThrows(InsufficientBalanceException.class, () -> specialistService.withdraw(1L, 200L));
    }

    @Test
    void withdraw_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specialistService.withdraw(1L, 100L));
    }
}
