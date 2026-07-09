package ir.HomeServiceApplication.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceDto {

    private Long id;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotNull(message = "Base price is required")
    private Long serviceBasePrice;

    private String serviceDescription;
    private Long parentServiceId;
}
