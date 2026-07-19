package ir.HomeServiceApplication.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    // only populated when role is SPECIALIST
    private String status;
    private Integer score;
    private List<String> services;
}
