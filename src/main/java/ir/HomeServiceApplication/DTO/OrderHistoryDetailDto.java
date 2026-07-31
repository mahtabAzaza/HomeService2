package ir.HomeServiceApplication.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderHistoryDetailDto {

    private Long id;
    private String orderDescription;
    private String address;
    private String orderStatus;
    private Long priceOffer;
    private Long finalPrice;
    private LocalDateTime orderStartDateTime;
    private LocalDateTime orderSubmitDateTime;
    private LocalDateTime completionTime;

    private Long serviceId;
    private String serviceName;

    private Long customerId;
    private String customerName;
    private String customerEmail;

    private Long specialistId;
    private String specialistName;
    private String specialistEmail;
}
