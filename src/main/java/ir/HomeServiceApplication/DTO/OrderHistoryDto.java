package ir.HomeServiceApplication.DTO;

import ir.HomeServiceApplication.entity.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public record OrderHistoryDto(
        Long orderId,
        String serviceName,
        String customerName,
        Long specialistId,
        Long customerId,
        Long serviceId,
        String specialistName,
        Long finalPrice,
        OrderStatus status,
        LocalDateTime orderDate,
        Long proposedPrice,
        LocalDateTime dateFrom,
        LocalDateTime dateTo
) {




}