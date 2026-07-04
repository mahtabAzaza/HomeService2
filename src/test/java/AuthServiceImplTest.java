
import DTO.CustomerResponseDto;
import DTO.CustomerSignupDto;
import DTO.SpecialistResponseDto;
import DTO.SpecialistSignupDto;
import entity.Customer;
import entity.Role;
import entity.Specialist;
import entity.SpecialistStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import repository.UserRepository;
import service.CustomerService;
import service.SpecialistService;
import service.serviceImpl.AuthServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private CustomerService customerService;
    @Mock private SpecialistService specialistService;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    // =====================================================
    // REGISTER CUSTOMER
    // =====================================================

    @Test
    void registerCustomer_shouldReturnResponse() {
        CustomerSignupDto dto = new CustomerSignupDto();
        CustomerResponseDto expected = new CustomerResponseDto();

        when(customerService.signup(dto)).thenReturn(expected);

        CustomerResponseDto result = authService.registerCustomer(dto);

        assertEquals(expected, result);
        verify(customerService).signup(dto);
    }

    // =====================================================
    // REGISTER SPECIALIST
    // =====================================================

    @Test
    void registerSpecialist_shouldReturnResponse() {
        SpecialistSignupDto dto = new SpecialistSignupDto();
        SpecialistResponseDto expected = new SpecialistResponseDto();

        when(specialistService.signup(dto)).thenReturn(expected);

        SpecialistResponseDto result = authService.registerSpecialist(dto);

        assertEquals(expected, result);
        verify(specialistService).signup(dto);
    }

    // =====================================================
    // LOAD USER BY USERNAME
    // =====================================================

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("none@mail.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> authService.loadUserByUsername("none@mail.com"));
    }

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
