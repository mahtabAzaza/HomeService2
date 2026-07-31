package ir.HomeServiceApplication.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderHistorySummaryDto {

    private Long id;
    private String serviceName;
    private String customerName;
    private String specialistName;
    private String orderStatus;
    private Long finalPrice;
    private LocalDateTime orderDate;
}
