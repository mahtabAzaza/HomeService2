package ir.HomeServiceApplication.service.serviceImpl;

import ir.HomeServiceApplication.DTO.CustomerResponseDto;
import ir.HomeServiceApplication.DTO.CustomerSignupDto;
import ir.HomeServiceApplication.DTO.SpecialistResponseDto;
import ir.HomeServiceApplication.DTO.SpecialistSignupDto;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.SpecialistStatus;
import ir.HomeServiceApplication.entity.User;
import ir.HomeServiceApplication.entity.VerificationToken;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotFoundException;
import ir.HomeServiceApplication.repository.UserRepository;
import ir.HomeServiceApplication.repository.VerificationTokenRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ir.HomeServiceApplication.service.AuthService;
import ir.HomeServiceApplication.service.CustomerService;
import ir.HomeServiceApplication.service.SpecialistService;
import ir.HomeServiceApplication.service.VerificationService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final CustomerService customerService;
    private final SpecialistService specialistService;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final VerificationService verificationService;

    public AuthServiceImpl(CustomerService customerService,
                           SpecialistService specialistService,
                           UserRepository userRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           EmailService emailService,
                           VerificationService verificationService) {
        this.customerService = customerService;
        this.specialistService = specialistService;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.verificationService = verificationService;
    }

    @Override
    public CustomerResponseDto registerCustomer(CustomerSignupDto dto) {
        CustomerResponseDto response = customerService.signup(dto);
        createAndSendVerificationToken(dto.getEmail());
        return response;
    }

    @Override
    public SpecialistResponseDto registerSpecialist(SpecialistSignupDto dto) {
        SpecialistResponseDto response = specialistService.signup(dto);
        createAndSendVerificationToken(dto.getEmail());
        return response;
    }

    private void createAndSendVerificationToken(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        token.setUsed(false);
        verificationTokenRepository.save(token);

        emailService.sendVerificationEmail(email, token.getToken());
    }

    @Override
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElse(null);

        if (verificationToken == null) {
            throw new NotFoundException("Invalid verification token");
        }
        if (verificationToken.isUsed()) {
            throw new InvalidOperationException("This verification link has already been used");
        }
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("This verification link has expired");
        }

        User user = userRepository.findByEmail(verificationToken.getUser().getEmail());
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        user.setEmailVerified(true);

        if (user instanceof Specialist specialist) {
            verificationService.refreshApprovalEligibility(specialist);
        }

        verificationToken.setUsed(true);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }

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
