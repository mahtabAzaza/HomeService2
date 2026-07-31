package ir.HomeServiceApplication.service.serviceImpl;

import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.SpecialistStatus;
import ir.HomeServiceApplication.entity.User;
import ir.HomeServiceApplication.entity.VerificationToken;
import ir.HomeServiceApplication.repository.VerificationTokenRepository;
import ir.HomeServiceApplication.service.VerificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationServiceImpl implements VerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    public VerificationServiceImpl(VerificationTokenRepository verificationTokenRepository,
                                    EmailService emailService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public void refreshApprovalEligibility(Specialist specialist) {
        // فقط متخصص‌های تازه‌ثبت‌نام (NEW) با این متد وارد صف تایید می‌شوند.
        // متخصص APPROVED یا DEACTIVATED نباید صرفاً با آپلود دوباره عکس یا تایید مجدد ایمیل
        // به‌طور ضمنی تغییر وضعیت بدهد — APPROVED با updateProfile و DEACTIVATED با تصمیم مدیر مدیریت می‌شود.
        if (specialist.getStatus() != SpecialistStatus.NEW) {
            return;
        }
        if (specialist.isEmailVerified() && specialist.getProfileImage() != null) {
            specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);
        }
    }

    @Override
    public void issueVerificationToken(User user) {
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        token.setUsed(false);
        verificationTokenRepository.save(token);

        emailService.sendVerificationEmail(user.getEmail(), token.getToken());
    }
}
