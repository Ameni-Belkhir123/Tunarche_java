package TunArche.entities;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaGenerator {

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;

    public static String generateCode(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        while (code.length() < length) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }

    public static Image generateCaptchaImage(String code) {
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();

        // Use java.awt.Color for LIGHT_GRAY
        g.setColor(Color.LIGHT_GRAY);  // Using LIGHT_GRAY from java.awt.Color
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Set font and color
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.setColor(Color.BLACK);
        g.drawString(code, 15, 28);

        // Noise lines
        Random rand = new Random();
        g.setColor(Color.GRAY);
        for (int i = 0; i < 5; i++) {
            int x1 = rand.nextInt(WIDTH);
            int y1 = rand.nextInt(HEIGHT);
            int x2 = rand.nextInt(WIDTH);
            int y2 = rand.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        g.dispose();

        // Convert BufferedImage to WritableImage
        WritableImage image = new WritableImage(WIDTH, HEIGHT);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int pixel = bufferedImage.getRGB(x, y);
                image.getPixelWriter().setColor(x, y, javafx.scene.paint.Color.rgb((pixel >> 16) & 0xFF, (pixel >> 8) & 0xFF, pixel & 0xFF));
            }
        }

        return image;
    }
}
