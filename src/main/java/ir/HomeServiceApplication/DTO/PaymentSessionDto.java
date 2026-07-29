package ir.HomeServiceApplication.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PaymentSessionDto {

    private String token;
    private String captchaImage;
    private String captcha;
    private Long orderId;
    private Long amount;
    private LocalDateTime expiresAt;
}
