package ir.HomeServiceApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSessionResponseDto {

    private String token;
    private String captchaImage;
    private Long amount;
    private LocalDateTime expiresAt;
}
