package ir.HomeServiceApplication.service;

import com.mewebstudio.captcha.Captcha;
import com.mewebstudio.captcha.Config;
import com.mewebstudio.captcha.GeneratedCaptcha;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class CaptchaService {

    private final Captcha captcha;

    public CaptchaService() {
        Config config = new Config(180, 60, 5, 4, false);
        config.setFontStyles(new int[]{Font.BOLD});
        // مسیر فونت‌های لاتین ویندوز — جلوگیری از انتخاب رندوم فونت فارسی
        config.setFonts(new String[]{
            "C:\\Windows\\Fonts\\arial.ttf",
            "C:\\Windows\\Fonts\\verdana.ttf",
            "C:\\Windows\\Fonts\\tahoma.ttf"
        });
        this.captcha = new Captcha(config);
    }

    public GeneratedCaptcha generate() {
        return captcha.generate();
    }

    public String toBase64(GeneratedCaptcha generated) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(generated.getImage(), "png", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode captcha image", e);
        }
    }
}
