package ir.HomeServiceApplication.DTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRequestDto {

    private String cardNumber;
    private String cvv2;
    private String expiry;
    private String password;
    private String captcha;
    private Long orderId;


}