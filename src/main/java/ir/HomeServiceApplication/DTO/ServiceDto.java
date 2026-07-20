package ir.HomeServiceApplication.DTO;

import ir.HomeServiceApplication.entity.Specialist;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

