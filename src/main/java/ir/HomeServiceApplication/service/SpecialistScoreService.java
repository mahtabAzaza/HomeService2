package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.Proposal;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.SpecialistStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class SpecialistScoreService {

    // وقتی مشتری نظر می‌دهد، امتیاز متخصص افزایش می‌یابد
    public void applyReviewScore(Specialist specialist, int score) {
        specialist.setScore(specialist.getScore() + score);
    }

    // وقتی سفارش دیرتر از زمان پیشنهادی متخصص تمام شده، امتیاز کسر می‌شود
    public void applyLateDeliveryPenalty(Order order) {
        Proposal selected = order.getSelectedProposal();
        if (selected == null || order.getCompletionTime() == null) {
            return;
        }

        LocalDateTime suggestedEnd = selected.getStartDate().plusHours(selected.getDuration());

        if (order.getCompletionTime().isAfter(suggestedEnd)) {
            long hoursLate = ChronoUnit.HOURS.between(suggestedEnd, order.getCompletionTime());
            Specialist specialist = order.getSpecialist();

            // به ازای هر ساعت تاخیر، ۱ امتیاز کسر می‌شود
            specialist.setScore(specialist.getScore() - (int) hoursLate);

            // اگر امتیاز منفی شد، حساب متخصص غیرفعال می‌شود
            if (specialist.getScore() < 0) {
                specialist.setStatus(SpecialistStatus.DEACTIVATED);
            }
        }
    }
}
