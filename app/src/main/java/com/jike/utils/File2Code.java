package com.jike.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by LiZhichao on 2015/12/29.
 */
public class File2Code {
    /**
     * <p>将文件转成base64 字符串</p>
     * @param path 文件路径
     * @return
     * @throws Exception
     */
//    public static String encodeBase64File(String path) throws Exception {
//        ByteArrayOutputStream baos = null ;
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        File  file = new File(path);
//        FileInputStream inputFile = new FileInputStream(file);
//        byte[] buffer = new byte[(int)file.length()];
//        inputFile.read(buffer);
//        inputFile.close();
//        return bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//    }

    /**
     *
     * @return
     */
    public static BitmapFactory.Options getBitmapOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTempStorage = new byte[1024 * 1024 * 10]; //10MB的临时存储空间
        return options;
    }

    /**
     * bitmap转为base64
     * @param path
     * @return
     */
    public static String bitmapToBase64(String path) {

        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options1);

        options1.inSampleSize = calculateInSampleSize(options1, 1024, 1280);  //110,160：转换后的宽和高，具体值会有些出入
        options1.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options1);

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                result= URLEncoder.encode(result, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                result= URLEncoder.encode(result, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     *
     * @param bitmap
     * @param maxSize  单位 kb
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap, double maxSize) {

        bitmap = imageZoom(bitmap, maxSize);

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                result= URLEncoder.encode(result, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap imageZoom(Bitmap bitMap, double maxSize) {
        //图片允许最大空间   单位：KB
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length/1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
                    bitMap.getHeight() / Math.sqrt(i));
        }
        return bitMap;
    }
    /***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap getBitmap(String absolutePath, Activity context) {
        BitmapFactory.Options opt = getBitmapOptions();
        //这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(absolutePath, opt);

        //获取到这个图片的原始宽度和高度
        /*int picWidth  = opt.outWidth;
        int picHeight = opt.outHeight;*/
        opt.inSampleSize = calculateInSampleSize(opt, 1024, 1280);
     /*   //获取屏的宽度和高度
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        //isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 2;
        //根据屏的大小和图片大小计算出缩放比例
        if(picWidth > picHeight){
            if(picWidth > screenWidth)
                opt.inSampleSize = picWidth/screenWidth;
        }
        else{
            if(picHeight > screenHeight)

                opt.inSampleSize = picHeight/screenHeight;
        }*/

        //这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(absolutePath, opt);

        return bitmap;
    }
}
