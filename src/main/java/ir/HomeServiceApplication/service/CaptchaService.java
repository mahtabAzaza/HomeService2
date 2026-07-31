package ir.HomeServiceApplication.service;

import com.mewebstudio.captcha.Captcha;
import com.mewebstudio.captcha.Config;
import com.mewebstudio.captcha.GeneratedCaptcha;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CaptchaService {

    // مسیرهای رایج فونت‌های لاتین به تفکیک سیستم‌عامل — فقط مسیرهایی که واقعاً روی دیسک وجود دارند انتخاب می‌شوند
    private static final List<String> WINDOWS_FONT_CANDIDATES = List.of(
            "C:\\Windows\\Fonts\\arial.ttf",
            "C:\\Windows\\Fonts\\verdana.ttf",
            "C:\\Windows\\Fonts\\tahoma.ttf"
    );

    private static final List<String> MAC_FONT_CANDIDATES = List.of(
            "/System/Library/Fonts/Supplemental/Arial.ttf",
            "/System/Library/Fonts/Supplemental/Verdana.ttf",
            "/Library/Fonts/Arial.ttf"
    );

    private static final List<String> LINUX_FONT_CANDIDATES = List.of(
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
            "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf",
            "/usr/share/fonts/TTF/DejaVuSans.ttf"
    );

    private final Captcha captcha;

    public CaptchaService() {
        Config config = new Config(180, 60, 5, 4, false);
        config.setFontStyles(new int[]{Font.BOLD});
        config.setFonts(resolveAvailableFonts());
        this.captcha = new Captcha(config);
    }

    // تشخیص سیستم‌عامل در زمان اجرا و انتخاب فقط فونت‌هایی که واقعاً روی دیسک موجودند
    private static String[] resolveAvailableFonts() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

        List<String> candidates;
        if (os.contains("win")) {
            candidates = WINDOWS_FONT_CANDIDATES;
        } else if (os.contains("mac")) {
            candidates = MAC_FONT_CANDIDATES;
        } else {
            candidates = LINUX_FONT_CANDIDATES;
        }

        List<String> existing = candidates.stream()
                .filter(path -> new File(path).isFile())
                .collect(Collectors.toList());

        if (existing.isEmpty()) {
            throw new IllegalStateException(
                    "No usable Latin TrueType font found on this system for captcha generation. " +
                    "Install one of: Arial/Verdana/Tahoma (Windows/macOS) or DejaVu Sans/Liberation Sans (Linux)."
            );
        }

        return existing.toArray(new String[0]);
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
