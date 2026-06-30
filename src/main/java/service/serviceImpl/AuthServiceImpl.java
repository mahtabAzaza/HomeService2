package service.serviceImpl;

import DTO.CustomerResponseDto;
import DTO.CustomerSignupDto;
import DTO.SpecialistResponseDto;
import DTO.SpecialistSignupDto;
import entity.Specialist;
import entity.SpecialistStatus;
import entity.User;
import exception.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.UserRepository;
import service.AuthService;
import service.CustomerService;
import service.SpecialistService;

@Service
@Transactional
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final CustomerService customerService;
    private final SpecialistService specialistService;
    private final UserRepository userRepository;

    public AuthServiceImpl(CustomerService customerService,
                           SpecialistService specialistService,
                           UserRepository userRepository) {
        this.customerService = customerService;
        this.specialistService = specialistService;
        this.userRepository = userRepository;
    }

    @Override
    public CustomerResponseDto registerCustomer(CustomerSignupDto dto) {
        return customerService.signup(dto);
    }

    @Override
    public SpecialistResponseDto registerSpecialist(SpecialistSignupDto dto) {
        return specialistService.signup(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }

        // Specialist must be APPROVED to log in
        if (user instanceof Specialist specialist) {
            if (specialist.getStatus() != SpecialistStatus.APPROVED) {
                throw new UsernameNotFoundException("Specialist account is not approved yet");
            }
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
