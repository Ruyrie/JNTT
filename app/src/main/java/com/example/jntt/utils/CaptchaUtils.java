package com.example.jntt.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Random;

/**
 * 项目职责：验证码工具，负责生成验证码文本和验证码图片。
 * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class CaptchaUtils {

    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static CaptchaUtils instance;
    private String code;

    /**
     * 项目职责：验证码工具，负责生成验证码文本和验证码图片。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    public static CaptchaUtils getInstance() {
        if (instance == null) {
            instance = new CaptchaUtils();
        }
        return instance;
    }

    /**
     * 项目职责：生成随机验证码字符串。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    public String getCode() {
        return code;
    }

    /**
     * 项目职责：根据验证码字符串生成带干扰线的验证码图片。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    public Bitmap createBitmap() {
        int width = 120;
        int height = 50;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor("#F0F0F0"));

        Paint paint = new Paint();
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);

        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            paint.setColor(getRandomColor());
            paint.setStrokeWidth(1.5f);
            canvas.drawLine(random.nextInt(width), random.nextInt(height),
                    random.nextInt(width), random.nextInt(height), paint);
        }

        // 绘制干扰点
        for (int i = 0; i < 50; i++) {
            paint.setColor(getRandomColor());
            canvas.drawPoint(random.nextInt(width), random.nextInt(height), paint);
        }

        // 生成验证码并绘制
        for (int i = 0; i < 4; i++) {
            char c = CHARS[random.nextInt(CHARS.length)];
            sb.append(c);
            paint.setColor(getRandomColor());
            // 随机旋转
            canvas.save();
            canvas.rotate(random.nextInt(20) - 10, 15 + i * 25, 35);
            canvas.drawText(String.valueOf(c), 15 + i * 25, 35, paint);
            canvas.restore();
        }

        code = sb.toString();
        return bitmap;
    }

    /**
     * 项目职责：为验证码干扰线或文字生成随机颜色。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private int getRandomColor() {
        Random random = new Random();
        int r = random.nextInt(150);
        int g = random.nextInt(150);
        int b = random.nextInt(150);
        return Color.rgb(r, g, b);
    }
}
