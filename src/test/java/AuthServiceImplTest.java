//import DTO.CustomerResponseDto;
//import DTO.CustomerSignupDto;
//import DTO.SpecialistResponseDto;
//import DTO.SpecialistSignupDto;
//import entity.Specialist;
//import entity.SpecialistStatus;
//import entity.User;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import static org.mockito.Mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import repository.UserRepository;
//import service.CustomerService;
//import service.SpecialistService;
//import service.serviceImpl.AuthServiceImpl;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceImplTest {
//
//    @Mock
//    private CustomerService customerService;
//
//    @Mock
//    private SpecialistService specialistService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private AuthServiceImpl authService;
//
//    @Test
//    void registerCustomer_shouldReturnResponse() {
//        CustomerSignupDto dto = new CustomerSignupDto();
//        CustomerResponseDto expected = new CustomerResponseDto();
//
//        when(customerService.signup(dto)).thenReturn(expected);
//
//        CustomerResponseDto result = authService.registerCustomer(dto);
//
//        assertEquals(expected, result);
//        verify(customerService).signup(dto);
//    }
//
//    @Test
//    void registerSpecialist_shouldReturnResponse() {
//        SpecialistSignupDto dto = new SpecialistSignupDto();
//        SpecialistResponseDto expected = new SpecialistResponseDto();
//
//        when(specialistService.signup(dto)).thenReturn(expected);
//
//        SpecialistResponseDto result = authService.registerSpecialist(dto);
//
//        assertEquals(expected, result);
//        verify(specialistService).signup(dto);
//    }
//
//    @Test
//    void loadUserByUsername_userNotFound_shouldThrowException() {
//        when(userRepository.findByEmail("test@mail.com")).thenReturn(null);
//
//        assertThrows(UsernameNotFoundException.class,
//                () -> authService.loadUserByUsername("test@mail.com"));
//    }
//
//    @Test
//    void loadUserByUsername_specialistNotApproved_shouldThrowException() {
//        Specialist specialist = mock(Specialist.class);
//        when(specialist.getStatus()).thenReturn(SpecialistStatus.WAITING_FOR_APPROVAL);
//        when(specialist.getEmail()).thenReturn("test@mail.com");
//        when(specialist.getPassword()).thenReturn("123");
//
//        when(userRepository.findByEmail("test@mail.com")).thenReturn(specialist);
//
//        assertThrows(UsernameNotFoundException.class,
//                () -> authService.loadUserByUsername("test@mail.com"));
//    }
//
//    @Test
//    void loadUserByUsername_validUser_shouldReturnUserDetails() {
//        User user = mock(User.class);
//
//        when(user.getEmail()).thenReturn("test@mail.com");
//        when(user.getPassword()).thenReturn("123");
//        when(user.getRole()).thenReturn(() -> "USER");
//
//        when(userRepository.findByEmail("test@mail.com")).thenReturn(user);
//
//        UserDetails result = authService.loadUserByUsername("test@mail.com");
//
//        assertEquals("test@mail.com", result.getUsername());
//        assertEquals("123", result.getPassword());
//    }
//}