package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.DTO.PaymentRequestDto;
import ir.HomeServiceApplication.entity.PaymentSession;

public interface PaymentService {

    PaymentSession createChargeSession(Long customerId, Long amount);

    PaymentSession findByToken(String token);

    PaymentSession pay(String token, PaymentRequestDto dto);
}
