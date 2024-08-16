package org.jacobwabrams;

import org.junit.Test;

public class JavaRGB565Test {

    /**
     * From stm32_lcd.c, Copyright (c) 2019-2020 STMicroelectronics.
     */
    private static int stm_CONVERTARGB88882RGB565(int Color) {
        return ((((Color & 0xFF) >> 3) & 0x1F) |
                (((((Color & 0xFF00) >> 8) >>2) & 0x3F) << 5) |
                (((((Color & 0xFF0000) >> 16) >>3) & 0x1F) << 11));
    }

    /**
     * From stm32_lcd.c, Copyright (c) 2019-2020 STMicroelectronics.
     */
    private static int stm_CONVERTRGB5652ARGB8888(int Color) {
        return (((((((Color >> 11) & 0x1F) * 527) + 23) >> 6) << 16) |
                ((((((Color >> 5) & 0x3F) * 259) + 33) >> 6) << 8) |
                ((((Color & 0x1F) * 527) + 23) >> 6) | 0xFF000000);
    }

    /**
     * No multiplication.
     * See https://stackoverflow.com/a/11471397/215266
     */
    private static int fast_CONVERTARGB88882RGB565(int Color) {
        int r = (Color & 0xff0000) >> 16;
        int g = (Color & 0xff00) >> 8;
        int b = (Color & 0xff);
        return ((r & 0b11111000) << 8) | ((g & 0b11111100) << 3) | (b >> 3);
    }

    /**
     * No multiplication.
     * See https://stackoverflow.com/a/2442617/215266
     */
    private static int fast_CONVERTRGB5652ARGB8888(int Color) {
        int r = ((Color & 0b11111000_00000000) >> 8);
        r |= (r >> 5);
        int g = ((Color & 0b111_11100000) >> 3);
        g |= (g >> 6);
        int b = (Color & 0b11111) << 3;
        b |= (b >> 5);
        return 0xFF << 24 | r << 16 | g << 8 | b;
    }

    /**
     * See https://developer.android.com/reference/android/graphics/Bitmap.Config?hl=en#RGB_565
     */
    public static int google_CONVERTARGB88882RGB565(int Color) {
        int R = (Color & 0xff0000) >> 16;
        int G = (Color & 0xff00) >> 8;
        int B = (Color & 0xff);
        return (R & 0x1f) << 11 | (G & 0x3f) << 5 | (B & 0x1f);
    }

    /**
     * Just calling {@link #fast_CONVERTRGB5652ARGB8888}
     */
    public static int google_CONVERTRGB5652ARGB8888(int Color) {
        // Nothing really makes sense anyway...
        return fast_CONVERTRGB5652ARGB8888(Color);
    }

    static final int[] colors888 = new int[] { 0xFFFFFFFF, 0xFF880000, 0xFF25002F, 0xFF4932CE, 0xFFD70966 };

    @Test
    public void stm888to565andBack() {
        for (int color888 : colors888) {
            int color565 = stm_CONVERTARGB88882RGB565(color888);
            int restored888 = stm_CONVERTRGB5652ARGB8888(color565);

            System.out.println(String.format("STM before888 0x%08x, after565 0x%08x, after888 0x%08x", color888, color565, restored888));
        }
    }

    @Test
    public void smart888to565andBack() {
        for (int color888 : colors888) {
            int color565 = fast_CONVERTARGB88882RGB565(color888);
            int restored888 = fast_CONVERTRGB5652ARGB8888(color565);

            System.out.println(String.format("Fast before888 0x%08x, after565 0x%08x, after888 0x%08x", color888, color565, restored888));
        }
    }

    @Test
    public void google888to565andBack() {
        for (int color888 : colors888) {
            int color565 = google_CONVERTARGB88882RGB565(color888);
            int restored888 = google_CONVERTRGB5652ARGB8888(color565);

            System.out.println(String.format("Google before888 0x%08x, after565 0x%08x, after888 0x%08x", color888, color565, restored888));
        }
    }

}
