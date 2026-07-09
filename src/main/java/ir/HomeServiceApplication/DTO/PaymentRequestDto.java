package ir.HomeServiceApplication.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRequestDto {

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "CVV2 is required")
    private String cvv2;

    @NotBlank(message = "Expiry is required")
    private String expiry;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Captcha is required")
    private String captcha;

    @NotNull(message = "Order ID is required")
    private Long orderId;
}