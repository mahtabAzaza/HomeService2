package service;

import entities.Specialist;
import entities.SpecialistStatus;
import repository.BaseRepository;
import java.util.Objects;

public class ApprovalService {

    // Repository برای دسترسی به دیتابیس متخصص‌ها
    // بعداً در Spring اینجا @Autowired یا constructor injection می‌گیریم
    private final BaseRepository<Specialist, Long> specialistRepository;

    public ApprovalService(BaseRepository<Specialist, Long> specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    /**
     *approve specialist by manager
     */
    public void approveSpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId);

        if (Objects.isNull(specialist)) {
            throw new RuntimeException("Specialist not found");
        }

        // اگر قبلاً تایید شده، دوباره کاری نکن
        if (specialist.getStatus() == SpecialistStatus.APPROVED) {
            return;
        }
        // تغییر وضعیت به تایید شده
        specialist.setStatus(SpecialistStatus.APPROVED);
        // ذخیره تغییرات
        specialistRepository.update(specialist);
    }

    /**
     *   put specialist back to waiting for approval
     */
    public void sendToWaitingApproval(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId);

        if (Objects.isNull(specialist)) {
            throw new RuntimeException("Specialist not found");
        }

        // فقط اگر approved بود یا new بود تغییر بده
        if (specialist.getStatus() == SpecialistStatus.WAITING_FOR_APPROVAL) {
            return;
        }

        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);

        specialistRepository.update(specialist);
    }
}