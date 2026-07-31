package ir.HomeServiceApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    private LocalDateTime expiryDate;

    private boolean used;

    // ManyToOne عمداً: یک کاربر می‌تواند طی زمان چند توکن داشته باشد
    // (مثلاً چند بار تغییر ایمیل قبل از تایید، یا ارسال دوباره ایمیل تایید در آینده)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}