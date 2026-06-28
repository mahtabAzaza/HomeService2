package DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpecialistSignupDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private byte[] profileImage;
}
