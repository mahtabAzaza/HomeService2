package ir.HomeServiceApplication.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = User.TABLE_NAME)
public abstract class User extends BaseEntity<Long> {

    public static final String TABLE_NAME = "users";

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

//    @NotBlank
//    @Size(min = 8, message = "Password must be at least 8 characters")
//    @Pattern(
//            regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
//            message = "Password must contain both letters and numbers"
//    )
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
