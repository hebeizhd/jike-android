package com.jike.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/21.
 */
public class FileUtils {
    /**
     * 获取原图片存储路径
     * @return
     */
    public static String getPhotopath() {
        // 照片全路径
        String fileName = "";
        String pathUrl = Environment.getExternalStorageDirectory() + "/jike/file/";
        String imageName = System.currentTimeMillis() + ".jpg";
        File file = new File(pathUrl);
        if (!file.exists()) {
            file.mkdirs();// 创建文件夹
        }
        fileName = pathUrl + imageName;
        return fileName;
    }

    public static String getCameraPhotoPath() {
        // 照片全路径
        String fileName = "";
        // 文件夹路径
        String pathUrl = "mnt/sdcard/DCIM/Camera/";
        String imageName = System.currentTimeMillis() + ".jpg";
        File file = new File(pathUrl);
        if (!file.exists()) {
            file.mkdirs();// 创建文件夹
        }
        fileName = pathUrl + imageName;
        return fileName;
    }

    /**
     * bitmap 缓存到本地
     * @param cachePath
     * @param pic
     */
    public static void bitmap2file(String cachePath, Bitmap pic) {
        File cacheFile = new File(cachePath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(cacheFile);
            pic.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param pic
     * @return
     */
    public static String bitmap2file(Bitmap pic) {
        String path = getPhotopath();
        File cacheFile = new File(path);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(cacheFile);
            pic.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * 把用户名和密码保存到手机ROM
     * @param value1 输入要保存的密码
     * @param value2 要保存的用户名
     * @param filename 保存到哪个文件
     * @return
     */
    public static boolean saveToRom(Context context, String value1, String value2, String filename) throws Exception{
        //以私有的方式打开一个文件
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        String result = value1 + ":" + value2;
        fos.write(result.getBytes());
        fos.flush();
        fos.close();
        return true;
    }

    public static Map<String,String> getUserInfo(String filename) throws Exception{
        File file = new File("data/data/com.jike/files/" + filename);
        Map<String,String> map = new HashMap<String,String>();
        if (!file.exists()) {
            return map;
        }
        FileInputStream fis = new FileInputStream(file);
        //以上的两句代码也可以通过以下的代码实现：
        //FileInputStream fis = context.openFileInput(filename);
        byte[] data = getBytes(fis);
        String result = new String(data);
        String results[] = result.split(":");
        map.put("value1", results[0]);
        map.put("value2", results[1]);
        return map;
    }

    public static void deleteCache(String filename){
        File file = new File("data/data/com.mimidai/files/" + filename);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 把InputStream中的内容读出来，放到一个byte[]中返回
     * @param is
     * @return
     * @throws Exception
     */
    public static byte[] getBytes(InputStream is) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len=is.read(buffer)) != -1){
            baos.write(buffer, 0, len);
        }
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }
}
