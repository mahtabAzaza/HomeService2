package ir.HomeServiceApplication.DTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceResponseDto {

        private Long id;
        private String serviceName;
        private Long serviceBasePrice;
        private String serviceDescription;
        private Long parentServiceId;
    }

