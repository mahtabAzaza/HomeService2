package service.serviceImpl;

import DTO.SpecialistSignupDto;
import entity.*;
import exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;
import service.SpecialistService;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SpecialistServiceImpl implements SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public SpecialistServiceImpl(SpecialistRepository specialistRepository,
                                 OrderRepository orderRepository,
                                 WalletRepository walletRepository,
                                 PasswordEncoder passwordEncoder) {
        this.specialistRepository = specialistRepository;
        this.orderRepository = orderRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signup(SpecialistSignupDto dto) {

        if (specialistRepository.findByEmail(dto.getEmail()) != null) {
            throw new DuplicateEmailException("Email already in use");
        }

        if (dto.getProfileImage() != null && dto.getProfileImage().length > 300 * 1024) {
            throw new InvalidOperationException("Profile image must be under 300 KB");
        }

        Wallet wallet = new Wallet();
        wallet.setBalance(0L);
        wallet.setOwnerName(dto.getFirstName() + " " + dto.getLastName());
        walletRepository.save(wallet);

        Specialist specialist = new Specialist();
        specialist.setFirstName(dto.getFirstName());
        specialist.setLastName(dto.getLastName());
        specialist.setEmail(dto.getEmail());
        specialist.setPassword(passwordEncoder.encode(dto.getPassword()));
        specialist.setProfileImage(dto.getProfileImage());
        specialist.setRegistrationDate(LocalDateTime.now());
        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);
        specialist.setRole(Role.SPECIALIST);
        specialist.setWallet(wallet);

        specialistRepository.save(specialist);
    }

    @Override
    @Transactional(readOnly = true)
    public Specialist login(String email, String password) {

        Specialist specialist = specialistRepository.findByEmail(email);

        if (specialist == null || !passwordEncoder.matches(password, specialist.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (specialist.getStatus() != SpecialistStatus.APPROVED) {
            throw new NotApprovedException("Your account is not approved yet");
        }

        return specialist;
    }

    @Override
    public void updateProfile(Long specialistId, SpecialistSignupDto dto) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        if (dto.getProfileImage() != null && dto.getProfileImage().length > 300 * 1024) {
            throw new InvalidOperationException("Profile image must be under 300 KB");
        }

        specialist.setFirstName(dto.getFirstName());
        specialist.setLastName(dto.getLastName());
        specialist.setEmail(dto.getEmail());
        specialist.setPassword(passwordEncoder.encode(dto.getPassword()));
        specialist.setProfileImage(dto.getProfileImage());

        // بعد از ویرایش اطلاعات باید دوباره تایید شود
        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAvailableOrders(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        if (specialist.getStatus() != SpecialistStatus.APPROVED) {
            throw new NotApprovedException("Your account is not approved yet");
        }

        return orderRepository.findByServiceInAndOrderStatus(
                specialist.getServices(),
                OrderStatus.WAITING_FOR_PROPOSAL
        );
    }

    @Override
    public void markOrderStarted(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.WAITING_FOR_SPECIALIST) {
            throw new InvalidOperationException("Order is not in the correct state to start");
        }

        order.setOrderStatus(OrderStatus.IN_PROGRESS);
    }

    @Override
    public void markOrderDone(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.IN_PROGRESS) {
            throw new InvalidOperationException("Order has not been started yet");
        }

        order.setOrderStatus(OrderStatus.DONE);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getWalletBalance(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        return specialist.getWallet().getBalance();
    }

    @Override
    public void withdraw(Long specialistId, Long amount) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        Wallet wallet = specialist.getWallet();

        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException("Not enough balance in wallet");
        }

        wallet.setBalance(wallet.getBalance() - amount);
    }
}
