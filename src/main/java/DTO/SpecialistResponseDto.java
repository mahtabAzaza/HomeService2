package DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpecialistResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private byte[] profileImage;
    private String status;
}
