package com.czh.tesseract;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class BitmapUtils {


    //Bitmap对象保存为图片文件
    public static void saveBitmapFile(Bitmap bitmap, String path){
        File file=new File(path);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * <图片按比例大小压缩方法（根据路径获取图片并压缩）>
     * <功能详细描述>
     *
     * @param srcPath
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Bitmap getImageByPath(String srcPath) {
        Bitmap bitmap = null;
        String time = null;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        // 高和宽设置为
        float minHeight = 800f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (width > height && width > minHeight) {
            // 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / minHeight);
        } else if (width < height && height > minHeight) {
            // 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / minHeight);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        return bitmap;// 压缩好比例大小后再进行质量压缩
    }

}
