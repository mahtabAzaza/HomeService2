package ir.HomeServiceApplication.controller;

import ir.HomeServiceApplication.DTO.PaymentLinkResponseDto;
import ir.HomeServiceApplication.DTO.PaymentRequestDto;
import ir.HomeServiceApplication.DTO.PaymentResultDto;
import ir.HomeServiceApplication.DTO.PaymentSessionResponseDto;
import ir.HomeServiceApplication.entity.PaymentSession;
import ir.HomeServiceApplication.mapper.PaymentMapper;
import ir.HomeServiceApplication.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping("/charge")
    public ResponseEntity<PaymentLinkResponseDto> charge(@RequestParam Long customerId,
                                                         @RequestParam Long amount) {
        PaymentSession session = paymentService.createChargeSession(customerId, amount);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentMapper.toLinkDto(session));
    }

    @GetMapping("/{token}")
    public PaymentSessionResponseDto get(@PathVariable String token) {
        return paymentMapper.toResponse(paymentService.findByToken(token));
    }

    @PostMapping("/{token}")
    public PaymentResultDto pay(@PathVariable String token,
                                @Valid @RequestBody PaymentRequestDto dto) {
        PaymentSession session = paymentService.pay(token, dto);
        return new PaymentResultDto(
                "Payment successful. Your wallet has been charged.",
                session.getStatus(),
                session.getAmount()
        );
    }
}
