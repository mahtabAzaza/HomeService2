package ir.HomeServiceApplication.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProposalDto {

    private Long id;

    @NotNull(message = "Proposal price is required")
    private Long proposalPrice;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "Duration is required")
    private Integer duration;

    private Long specialistId;

    @NotNull(message = "Order ID is required")
    private Long orderId;
}
