package ir.HomeServiceApplication.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WalletTransactionDto {

    private Long id;
    private Long amount;
    private String type;
    private String description;
    private LocalDateTime createdAt;
}
