package ir.HomeServiceApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsufficientBalanceResponseDto {

    private String message;
    private Long currentBalance;
    private Long requiredAmount;
    private Long neededAmount;
    private String chargeUrl;
}