package DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerSignupDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private byte[] profilePicture;
}
