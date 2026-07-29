package ir.HomeServiceApplication.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CaptchaResponseDto {

    // توکن یکتا برای شناسایی session پرداخت
    private String token;

    // تصویر کپچا به صورت base64
    private String captchaImage;
}
