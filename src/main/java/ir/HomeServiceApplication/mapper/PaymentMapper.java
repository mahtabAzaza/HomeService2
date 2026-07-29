package ir.HomeServiceApplication.mapper;

import ir.HomeServiceApplication.DTO.PaymentLinkResponseDto;
import ir.HomeServiceApplication.DTO.PaymentSessionResponseDto;
import ir.HomeServiceApplication.entity.PaymentSession;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentLinkResponseDto toLinkDto(PaymentSession session) {
        String paymentUrl = "/payment.html?token=" + session.getToken();
        return new PaymentLinkResponseDto(session.getToken(), paymentUrl);
    }

    public PaymentSessionResponseDto toResponse(PaymentSession session) {
        return new PaymentSessionResponseDto(
                session.getToken(),
                session.getCaptchaImage(),
                session.getAmount(),
                session.getExpiresAt()
        );
    }
}
