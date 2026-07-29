package ir.HomeServiceApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_sessions")
public class PaymentSession extends BaseEntity<Long> {

    @Column(unique = true, nullable = false)
    private String token;

    private Long customerId;

    private Long amount;

    @Column(nullable = false)
    private LocalDateTime paymentStartTime;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private Long orderId;

    // کد کپچا (متن) برای اعتبارسنجی سمت سرور
    @Column(nullable = false)
    private String captcha;

    // تصویر base64 کپچا — برای نمایش در payment.html
    @Column(columnDefinition = "TEXT")
    private String captchaImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
}
