package ir.HomeServiceApplication.service.serviceImpl;

import com.mewebstudio.captcha.GeneratedCaptcha;
import ir.HomeServiceApplication.DTO.PaymentRequestDto;
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotFoundException;
import ir.HomeServiceApplication.repository.PaymentRepository;
import ir.HomeServiceApplication.repository.WalletRepository;
import ir.HomeServiceApplication.service.CaptchaService;
import ir.HomeServiceApplication.service.PaymentService;
import ir.HomeServiceApplication.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final PaymentRepository paymentRepository;
    private final CaptchaService captchaService;

    public PaymentServiceImpl(WalletService walletService,
                              WalletRepository walletRepository,
                              PaymentRepository paymentRepository,
                              CaptchaService captchaService) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.paymentRepository = paymentRepository;
        this.captchaService = captchaService;
    }

    @Override
    public PaymentSession createChargeSession(Long customerId, Long amount) {
        if (amount <= 0) {
            throw new InvalidOperationException("Amount must be positive.");
        }

        GeneratedCaptcha generated = captchaService.generate();
        LocalDateTime now = LocalDateTime.now();
        PaymentSession session = new PaymentSession();
        session.setToken(UUID.randomUUID().toString());
        session.setCustomerId(customerId);
        session.setAmount(amount);
        session.setCaptcha(generated.getCode());
        session.setCaptchaImage(captchaService.toBase64(generated));
        session.setPaymentStartTime(now);
        session.setExpiresAt(now.plusMinutes(10));
        session.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(session);
    }

    @Override
    public PaymentSession findByToken(String token) {
        PaymentSession session = paymentRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Payment session not found."));

        if (session.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidOperationException("Payment session already used or expired.");
        }

        if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
            session.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(session);
            throw new InvalidOperationException("Payment session has expired.");
        }

        return session;
    }

    @Override
    public PaymentSession pay(String token, PaymentRequestDto dto) {
        PaymentSession session = paymentRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Payment session not found."));

        if (session.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidOperationException("Payment session already used or expired.");
        }

        if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
            session.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(session);
            throw new InvalidOperationException("Payment time has expired. Please reload the payment page.");
        }

        if (!session.getCaptcha().equalsIgnoreCase(dto.getCaptcha())) {
            throw new InvalidOperationException("Invalid captcha.");
        }

        Wallet wallet = walletRepository.findByCustomer_Id(session.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Wallet not found for customer."));
        walletService.chargeWallet(wallet.getId(), session.getAmount());

        session.setStatus(PaymentStatus.COMPLETED);
        return paymentRepository.save(session);
    }
}
