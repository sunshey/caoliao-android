package com.yc.liaolive.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yc.liaolive.contants.NetContants;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * ImageUtils
 * <ul>
 * convert between Bitmap, byte array, Drawable
 * <li>{@link #bitmapToByte(Bitmap)}</li>
 * <li>{@link #bitmapToDrawable(Bitmap)}</li>
 * <li>{@link #byteToBitmap(byte[])}</li>
 * <li>{@link #byteToDrawable(byte[])}</li>
 * <li>{@link #drawableToBitmap(Drawable)}</li>
 * <li>{@link #drawableToByte(Drawable)}</li>
 * </ul>
 * </ul>
 * <ul>
 * scale image
 * <li>{@link #scaleImageTo(Bitmap, int, int)}</li>
 * <li>{@link #scaleImage(Bitmap, float, float)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2012-6-27
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    private ImageUtils() {
        throw new AssertionError();
    }

    /**
     * convert Bitmap to byte array
     *
     * @param b
     * @return
     */
    public static byte[] bitmapToByte(Bitmap b) {
        if (b == null) {
            return null;
        }

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, o);
        return o.toByteArray();
    }

    /**
     * convert byte array to Bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * convert Drawable to Bitmap
     *
     * @param d
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable d) {
        return d == null ? null : ((BitmapDrawable) d).getBitmap();
    }

    /**
     * convert Bitmap to Drawable
     *
     * @param b
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap b) {
        return b == null ? null : new BitmapDrawable(b);
    }

    /**
     * convert Drawable to byte array
     *
     * @param d
     * @return
     */
    public static byte[] drawableToByte(Drawable d) {
        return bitmapToByte(drawableToBitmap(d));
    }

    /**
     * convert byte array to Drawable
     *
     * @param b
     * @return
     */
    public static Drawable byteToDrawable(byte[] b) {
        return bitmapToDrawable(byteToBitmap(b));
    }


    /**
     * scale image
     *
     * @param org
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * scale image
     *
     * @param org
     * @param scaleWidth  sacle of width
     * @param scaleHeight scale of height
     * @return
     */
    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }


    /**
     * 根据URL获取BITMAP对象
     * @param imgUrl
     * @return
     */
    public static Bitmap getBitmapFromUrl(String imgUrl) {
        URL url;
        Bitmap bitmap = null;
        try {
            url = new URL(imgUrl);
            InputStream is = url.openConnection().getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            byte[] b = getBytes(is);
            bitmap = BitmapFactory.decodeByteArray(b,0,b.length);
            bis.close();

        }catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e){

            e.printStackTrace();
        }
        return bitmap;
    }

    /**

     * 将InputStream对象转换为Byte[]
     * @param is
     * @return
     * @throws IOException */
    public static byte[] getBytes(InputStream is) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new  byte[1024];
        int len = 0;
        while ((len = is.read(b, 0, 1024)) != -1)
        {
            baos.write(b, 0, len);
            baos.flush();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * 将图片压缩至指定大小以内
     *
     * @param image
     * @param size  单位为kb
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int size) {
        //图片允许最大空间   单位：KB
        double maxSize = size;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length / 1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            image = zoomImageMakeImage(image, image.getWidth() / Math.sqrt(i),
                    image.getHeight() / Math.sqrt(i));
        }
        return image;
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImageMakeImage(Bitmap bgimage, double newWidth,
                                            double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 批量压缩图像
     * @param files
     * @return
     */

    public static List<File> changeFileSize(List<File> files) {

        List<File> resultFiles = new ArrayList<File>();

        if (files != null && files.size() > 0) {
            try {
                for (int i = 0; i < files.size(); i++) {

                    BitmapFactory.Options options = new BitmapFactory.Options();

                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(files.get(i).getAbsolutePath(), options);

                    if (options.outWidth > 450 || options.outHeight > 800) {
                        String fileName = String.valueOf(TimeUtils.getCurrentTimeInLong()) + (int) (Math.random() * (9999 - 1000 + 1)) + 1000 + ".jpg";
                        String outPath = NetContants.CACHE_PATH + fileName;
                        Log.d(TAG,fileName);
                        Log.d(TAG,outPath);
                        options.inJustDecodeBounds = false;
                        Bitmap temp = BitmapFactory.decodeFile(files.get(i).getAbsolutePath(), options);
                        if (temp != null) {
                            File fileDir = new File(NetContants.CACHE_PATH);
                            if (!fileDir.exists()) {
                                fileDir.mkdirs();
                            }

                            File tempFile = new File(outPath);
                            if (!tempFile.exists()) {
                                tempFile.createNewFile();
                            }
                            ImageFactory.ratioAndGenThumb(temp, outPath, 500,800);
                            resultFiles.add(tempFile);
                        }
                    } else {
                        resultFiles.add(files.get(i));
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultFiles;
    }




    public static String changeFileSizeByLocalPath(String sfilePath) {
        String outPath = null;
        if (sfilePath != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(sfilePath, options);
                if (options.outWidth > 1000 || options.outHeight > 1000) {
                    String fileName = "edit_image_temp.jpg";
                    outPath = NetContants.CACHE_PATH +fileName;
                    options.inJustDecodeBounds = false;
                    Bitmap temp = BitmapFactory.decodeFile(sfilePath, options);
                    if (temp != null) {

                        File fileDir = new File(NetContants.CACHE_PATH);
                        if (!fileDir.exists()) {
                            fileDir.mkdirs();
                        }

                        File tempFile = new File(outPath);
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                        tempFile.createNewFile();
                        ImageFactory.ratioAndGenThumb(temp, outPath, 200,200);
                    }
                }else{
                    outPath = sfilePath;
                }
            } catch (IOException e) {
                e.printStackTrace();
                outPath = null;
            }
        }
        return outPath;
    }


    public static String changeFileSizeByLocalPath1080(String sfilePath) {
        String outPath = null;
        if (sfilePath != null) {
            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(sfilePath, options);

                if (options.outWidth > 1920|| options.outHeight > 1080) {
                    String fileName = "edit_image_temp.jpg";
                    outPath = NetContants.CACHE_PATH +fileName;
                    options.inJustDecodeBounds = false;
                    Bitmap temp = BitmapFactory.decodeFile(sfilePath, options);
                    if (temp != null) {

                        File fileDir = new File(NetContants.CACHE_PATH);
                        if (!fileDir.exists()) {
                            fileDir.mkdirs();
                        }

                        File tempFile = new File(outPath);
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                        tempFile.createNewFile();
                        ImageFactory.ratioAndGenThumb(temp, outPath, 200,200);
                    }
                }else{
                    outPath = sfilePath;
                }
            } catch (IOException e) {
                e.printStackTrace();
                outPath = null;
            }
        }
        return outPath;
    }


    public static Bitmap getBitmapFromFile(File dst, int width, int height) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();//设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength,
                        width * height);//这里一定要将其设置回false，因为之前我们将其设置成了true
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    /**
     * 对图片模糊处理
     * @param context
     * @param bitmap
     * @return
     */
    public static Drawable compresBitmap(Context context, Bitmap bitmap) {
        if(null==bitmap){
            return null;
        }
        final float widthHeightSize = (float) (DisplayUtil.getScreenWidth(context)
                * 1.0 / DisplayUtil.getScreenHeight(context) * 1.0);
        int cropBitmapWidth = (int) (widthHeightSize * bitmap.getHeight());
        int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);
        //切割部分图片
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth,
                bitmap.getHeight());
        //缩小图片
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 50, bitmap
                .getHeight() / 50, false);
        //模糊处理
        final Bitmap blurBitmap = FastBlurUtil.doBlur(scaleBitmap, 8, true);

        final Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
        //加入灰色遮罩层，避免图片过亮影响其他控件
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        return foregroundDrawable;
    }

//    /**
//     * 以高斯模糊显示。
//     *
//     * @param draweeView View。
//     * @param url        url.
//     * @param iterations 迭代次数，越大越魔化。
//     * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
//     */
//    public static void setBlurImage(SimpleDraweeView draweeView, String url, int iterations, int blurRadius) {
//        try {
//            Uri uri = Uri.parse(url);
//            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
//                    .setPostprocessor(new IterativeBoxBlurPostProcessor(iterations, blurRadius))
//                    .build();
//            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
//                    .setOldController(draweeView.getController())
//                    .setImageRequest(request)
//                    .build();
//            draweeView.setController(controller);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
