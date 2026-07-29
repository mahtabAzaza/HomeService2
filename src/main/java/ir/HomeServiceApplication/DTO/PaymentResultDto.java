package ir.HomeServiceApplication.DTO;

import ir.HomeServiceApplication.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultDto {

    private String message;
    private PaymentStatus status;
    private Long amount;
}
