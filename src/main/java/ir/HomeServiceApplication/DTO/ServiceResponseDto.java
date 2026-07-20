package ir.HomeServiceApplication.DTO;

import ir.HomeServiceApplication.entity.Specialist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ServiceResponseDto {

    private Long id;
    private String serviceName;
    private Long serviceBasePrice;
    private String serviceDescription;
    private Long parentServiceId;
    private List<ServiceResponseDto> childServices;
}

