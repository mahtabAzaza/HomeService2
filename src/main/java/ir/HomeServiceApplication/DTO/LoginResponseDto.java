package ir.HomeServiceApplication.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDto {

    private String token;
    private String role;
    private Object user;

    public LoginResponseDto(String token, String role, Object user) {
        this.token = token;
        this.role = role;
        this.user = user;
    }
}