
import ir.HomeServiceApplication.DTO.CustomerResponseDto;
import ir.HomeServiceApplication.DTO.CustomerSignupDto;
import ir.HomeServiceApplication.DTO.SpecialistResponseDto;
import ir.HomeServiceApplication.DTO.SpecialistSignupDto;
import ir.HomeServiceApplication.entity.Customer;
import ir.HomeServiceApplication.entity.Role;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.SpecialistStatus;
import ir.HomeServiceApplication.entity.VerificationToken;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ir.HomeServiceApplication.repository.UserRepository;
import ir.HomeServiceApplication.repository.VerificationTokenRepository;
import ir.HomeServiceApplication.service.CustomerService;
import ir.HomeServiceApplication.service.SpecialistService;
import ir.HomeServiceApplication.service.VerificationService;
import ir.HomeServiceApplication.service.serviceImpl.AuthServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private CustomerService customerService;
    @Mock private SpecialistService specialistService;
    @Mock private UserRepository userRepository;
    @Mock private VerificationTokenRepository verificationTokenRepository;
    @Mock private VerificationService verificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    // =====================================================
    // REGISTER CUSTOMER
    // =====================================================

    // Delegates to CustomerService.signup, returns the resulting DTO, and issues a verification token
    @Test
    void registerCustomer_shouldReturnResponse() {
        CustomerSignupDto dto = new CustomerSignupDto();
        dto.setEmail("cust@mail.com");
        CustomerResponseDto expected = new CustomerResponseDto();
        Customer customer = new Customer();
        customer.setEmail("cust@mail.com");

        when(customerService.signup(dto)).thenReturn(expected);
        when(userRepository.findByEmail("cust@mail.com")).thenReturn(customer);

        CustomerResponseDto result = authService.registerCustomer(dto);

        assertEquals(expected, result);
        verify(customerService).signup(dto);
        verify(verificationService).issueVerificationToken(customer);
    }

    // =====================================================
    // REGISTER SPECIALIST
    // =====================================================

    // Delegates to SpecialistService.signup, returns the resulting DTO, and issues a verification token
    @Test
    void registerSpecialist_shouldReturnResponse() {
        SpecialistSignupDto dto = new SpecialistSignupDto();
        dto.setEmail("spec@mail.com");
        SpecialistResponseDto expected = new SpecialistResponseDto();
        Specialist specialist = new Specialist();
        specialist.setEmail("spec@mail.com");

        when(specialistService.signup(dto)).thenReturn(expected);
        when(userRepository.findByEmail("spec@mail.com")).thenReturn(specialist);

        SpecialistResponseDto result = authService.registerSpecialist(dto);

        assertEquals(expected, result);
        verify(specialistService).signup(dto);
        verify(verificationService).issueVerificationToken(specialist);
    }

    // Throws NotFoundException when the newly signed-up user cannot be looked back up by email
    @Test
    void registerCustomer_shouldThrow_whenUserNotFoundAfterSignup() {
        CustomerSignupDto dto = new CustomerSignupDto();
        dto.setEmail("ghost@mail.com");
        CustomerResponseDto expected = new CustomerResponseDto();

        when(customerService.signup(dto)).thenReturn(expected);
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> authService.registerCustomer(dto));
    }

    // =====================================================
    // VERIFY EMAIL
    // =====================================================

    // Marks the user verified and consumes the token when it is valid and unexpired
    @Test
    void verifyEmail_shouldMarkUserVerified_whenTokenValid() {
        Customer customer = new Customer();
        customer.setEmail("cust@mail.com");
        customer.setEmailVerified(false);

        VerificationToken token = new VerificationToken();
        token.setToken("abc-123");
        token.setUser(customer);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        token.setUsed(false);

        when(verificationTokenRepository.findByToken("abc-123")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("cust@mail.com")).thenReturn(customer);

        authService.verifyEmail("abc-123");

        assertTrue(customer.isEmailVerified());
        assertTrue(token.isUsed());
    }

    // Also refreshes approval eligibility when the verified user is a specialist
    @Test
    void verifyEmail_shouldRefreshApprovalEligibility_whenUserIsSpecialist() {
        Specialist specialist = new Specialist();
        specialist.setEmail("spec@mail.com");
        specialist.setEmailVerified(false);

        VerificationToken token = new VerificationToken();
        token.setToken("abc-123");
        token.setUser(specialist);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        token.setUsed(false);

        when(verificationTokenRepository.findByToken("abc-123")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("spec@mail.com")).thenReturn(specialist);

        authService.verifyEmail("abc-123");

        assertTrue(specialist.isEmailVerified());
        verify(verificationService).refreshApprovalEligibility(specialist);
    }

    // Throws NotFoundException when the token does not exist
    @Test
    void verifyEmail_shouldThrow_whenTokenNotFound() {
        when(verificationTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.verifyEmail("missing"));
    }

    // Throws InvalidOperationException when the token was already used
    @Test
    void verifyEmail_shouldThrow_whenTokenAlreadyUsed() {
        VerificationToken token = new VerificationToken();
        token.setToken("abc-123");
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        token.setUsed(true);

        when(verificationTokenRepository.findByToken("abc-123")).thenReturn(Optional.of(token));

        assertThrows(InvalidOperationException.class, () -> authService.verifyEmail("abc-123"));
    }

    // Throws InvalidOperationException when the token has expired
    @Test
    void verifyEmail_shouldThrow_whenTokenExpired() {
        VerificationToken token = new VerificationToken();
        token.setToken("abc-123");
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        token.setUsed(false);

        when(verificationTokenRepository.findByToken("abc-123")).thenReturn(Optional.of(token));

        assertThrows(InvalidOperationException.class, () -> authService.verifyEmail("abc-123"));
    }

    // =====================================================
    // LOAD USER BY USERNAME
    // =====================================================

    // Throws UsernameNotFoundException when the email does not exist in the repository
    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("none@mail.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> authService.loadUserByUsername("none@mail.com"));
    }

    // Throws UsernameNotFoundException when the specialist account is still awaiting approval
    @Test
    void loadUserByUsername_shouldThrowException_whenSpecialistNotApproved() {
        Specialist specialist = new Specialist();
        specialist.setEmail("spec@mail.com");
        specialist.setPassword("encoded");
        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);
        specialist.setRole(Role.SPECIALIST);

        when(userRepository.findByEmail("spec@mail.com")).thenReturn(specialist);

        assertThrows(UsernameNotFoundException.class,
                () -> authService.loadUserByUsername("spec@mail.com"));
    }

    // Returns a UserDetails object with the correct username and encoded password for a customer
    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenCustomer() {
        Customer customer = new Customer();
        customer.setEmail("cust@mail.com");
        customer.setPassword("encoded");
        customer.setRole(Role.CUSTOMER);

        when(userRepository.findByEmail("cust@mail.com")).thenReturn(customer);

        UserDetails result = authService.loadUserByUsername("cust@mail.com");

        assertEquals("cust@mail.com", result.getUsername());
        assertEquals("encoded", result.getPassword());
    }

    // Returns a UserDetails object for a specialist whose status is APPROVED
    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenApprovedSpecialist() {
        Specialist specialist = new Specialist();
        specialist.setEmail("spec@mail.com");
        specialist.setPassword("encoded");
        specialist.setStatus(SpecialistStatus.APPROVED);
        specialist.setRole(Role.SPECIALIST);

        when(userRepository.findByEmail("spec@mail.com")).thenReturn(specialist);

        UserDetails result = authService.loadUserByUsername("spec@mail.com");

        assertEquals("spec@mail.com", result.getUsername());
    }
}
