package com.yc.liaolive.view.widget;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Generate thumb and background color state list use tintColor
 * Created by kyle on 15/11/4.
 */
public class ColorUtils {
    private static final int ENABLE_ATTR = android.R.attr.state_enabled;
    private static final int CHECKED_ATTR = android.R.attr.state_checked;
    private static final int PRESSED_ATTR = android.R.attr.state_pressed;

    static ColorStateList generateThumbColorWithTintColor(final int tintColor) {
        int[][] states = new int[][]{
                {-ENABLE_ATTR, CHECKED_ATTR},
                {-ENABLE_ATTR},
                {PRESSED_ATTR, -CHECKED_ATTR},
                {PRESSED_ATTR, CHECKED_ATTR},
                {CHECKED_ATTR},
                {-CHECKED_ATTR}
        };

        int[] colors = new int[]{
                tintColor - 0xAA000000,
                0xFFBABABA,
                tintColor - 0x99000000,
                tintColor - 0x99000000,
                tintColor | 0xFF000000,
                0xFFEEEEEE
        };
        return new ColorStateList(states, colors);
    }

    static ColorStateList generateBackColorWithTintColor(final int tintColor) {
        int[][] states = new int[][]{
                {-ENABLE_ATTR, CHECKED_ATTR},
                {-ENABLE_ATTR},
                {CHECKED_ATTR, PRESSED_ATTR},
                {-CHECKED_ATTR, PRESSED_ATTR},
                {CHECKED_ATTR},
                {-CHECKED_ATTR}
        };

        int[] colors = new int[]{
                tintColor - 0xE1000000,
                0x10000000,
                tintColor - 0xD0000000,
                0x20000000,
                tintColor - 0xD0000000,
                0x20000000
        };
        return new ColorStateList(states, colors);
    }

    private static final String TAG = "GBData";
    static ImageReader reader;
    private static Bitmap bitmap;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static int getColor(int x, int y) {
        if (reader == null) {
            Log.w(TAG, "getColor: reader is null");
            return -1;
        }

        Image image = reader.acquireLatestImage();

        if (image == null) {
            if (bitmap == null) {
                Log.w(TAG, "getColor: image is null");
                return -1;
            }
            return bitmap.getPixel(x, y);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        }
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();

        return bitmap.getPixel(x, y);
    }
}
