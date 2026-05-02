package com.taolife.common.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;

/**
 * 图形验证码生成器
 */
@Slf4j
public class ImageCaptchaUtil {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int LINE_COUNT = 20;
    private static final int FONT_SIZE = 30;
    private static final String FONT_NAME = "Arial";
    private static final String IMAGE_FORMAT = "JPEG";

    private static final Random random = new Random();

    /**
     * 生成验证码图片
     * 
     * @param code 验证码文本
     * @return base64编码的图片
     */
    public static String generateImage(String code) {
        if (code == null) {
            log.error("验证码文本为空，无法生成图片");
            return null;
        }

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(getRandomColor(200, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        for (int i = 0; i < LINE_COUNT; i++) {
            g.setColor(getRandomColor(160, 200));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制验证码
        g.setFont(new Font(FONT_NAME, Font.BOLD, FONT_SIZE));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(getRandomColor(20, 130));
            g.drawString(String.valueOf(code.charAt(i)), 20 * i + 10, 30);
        }

        g.dispose();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, IMAGE_FORMAT, baos);
            log.info("生成验证码图片成功: {}", code);
            // 将图片转换为Base64编码
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.error("生成验证码图片失败：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }

    private static Color getRandomColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}