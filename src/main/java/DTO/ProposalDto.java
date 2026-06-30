package DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProposalDto {

    private Long id;
    private Long proposalPrice;
    private LocalDateTime startDate;
    private Integer duration;
    private Long specialistId;
    private Long orderId;
}
