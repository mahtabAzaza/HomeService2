package ir.HomeServiceApplication.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

@Service
public class CaptchaService {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int CAPTCHA_LENGTH = 5;

    private final SecureRandom random = new SecureRandom();

    public byte[] generateCaptcha(HttpSession session) throws IOException {

        String captcha = generateCaptchaText();

        session.setAttribute("captcha", captcha);

        BufferedImage image = createCaptchaImage(captcha);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ImageIO.write(image, "png", outputStream);

        return outputStream.toByteArray();
    }

    private String generateCaptchaText() {

        StringBuilder captcha = new StringBuilder();

        for (int i = 0; i < CAPTCHA_LENGTH; i++) {

            int index = random.nextInt(CHARACTERS.length());

            captcha.append(CHARACTERS.charAt(index));
        }

        return captcha.toString();
    }

    private BufferedImage createCaptchaImage(String captchaText) {

        BufferedImage image =
                new BufferedImage(200, 60, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 200, 60);

        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.BOLD, 30));

        graphics.drawString(captchaText, 40, 40);

        graphics.drawLine(20, 10, 180, 40);
        graphics.drawLine(0, 50, 200, 20);

        graphics.dispose();

        return image;
    }
}



