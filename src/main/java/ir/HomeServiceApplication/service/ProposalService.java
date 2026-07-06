package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.entity.Proposal;

import java.time.LocalDateTime;
import java.util.List;

public interface ProposalService {

    // ثبت پیشنهاد توسط متخصص
    void submitProposal(Long specialistId, Long orderId, Long price,
                        LocalDateTime startDate, Integer duration);

    // مشاهده پیشنهادهای یک سفارش (مرتب‌شده بر اساس قیمت یا امتیاز متخصص)
    List<Proposal> getProposalsForOrder(Long orderId, String sortBy);
}
