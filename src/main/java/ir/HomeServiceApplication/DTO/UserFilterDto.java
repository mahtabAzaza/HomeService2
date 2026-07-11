package ir.HomeServiceApplication.DTO;

import ir.HomeServiceApplication.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserFilterDto {

    private Role role;
    private String firstName;
    private String lastName;
    private String serviceName;
    private Integer minScore;
    private Integer maxScore;
}
