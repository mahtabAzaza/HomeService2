package ir.HomeServiceApplication.service.serviceImpl;

import ir.HomeServiceApplication.DTO.SpecialistResponseDto;
import ir.HomeServiceApplication.DTO.SpecialistSignupDto;
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.exception.*;
import ir.HomeServiceApplication.mapper.SpecialistMapper;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.SpecialistRepository;
import ir.HomeServiceApplication.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ir.HomeServiceApplication.service.SpecialistService;

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


    // ثبت نام
    @Override
    public SpecialistResponseDto signup(SpecialistSignupDto dto) {

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
        // اگر عکس پروفایل آپلود نشده باشد، وضعیت NEW است تا بعداً عکس آپلود کند
        // اگر عکس آپلود شده باشد، وضعیت WAITING_FOR_APPROVAL برای تایید مدیر
        specialist.setStatus(dto.getProfileImage() == null
                ? SpecialistStatus.NEW
                : SpecialistStatus.WAITING_FOR_APPROVAL);
        specialist.setRole(Role.SPECIALIST);
        specialist.setWallet(wallet);

        specialistRepository.save(specialist);
        return SpecialistMapper.toDto(specialist);
    }

    @Override
    @Transactional(readOnly = true)
    public Specialist findByEmail(String email) {
        return specialistRepository.findByEmail(email);
    }


    // ورود
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

    // ویرایش اطلاعات
    @Override
    public void updateProfile(Long specialistId, SpecialistSignupDto dto) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        boolean hasOpenOrder = orderRepository.existsBySpecialistIdAndOrderStatusIn(
                specialistId,
                List.of(OrderStatus.WAITING_FOR_SPECIALIST, OrderStatus.IN_PROGRESS)
        );
        if (hasOpenOrder) {
            throw new InvalidOperationException("Cannot update profile while you have an open order");
        }

        if (dto.getProfileImage() != null && dto.getProfileImage().length > 300 * 1024) {
            throw new InvalidOperationException("Profile image must be under 300 KB");
        }

        specialist.setEmail(dto.getEmail());

        specialist.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getProfileImage() != null) {
            specialist.setProfileImage(dto.getProfileImage());
            // آپلود یا به‌روزرسانی عکس پروفایل → باید دوباره توسط مدیر تایید شود
            specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);
        }
        // اگر عکسی ارسال نشده، وضعیت تغییر نمی‌کند
    }
// دیدن سفارشات
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

    // مشاهده موجودی
    @Override
    @Transactional(readOnly = true)
    public Long getWalletBalance(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        return specialist.getWallet().getBalance();
    }

    // برداشت
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
    // امتیاز متخصص
    public void score(Long specialistId) {

    }
}
