package DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderDto {

    private Long id;
    private String orderDescription;
    private Long priceOffer;
    private LocalDateTime orderStartDateTime;
    private String address;
    private String orderStatus;
    private Long serviceId;
}
