package com.hzaz.base.common_util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import com.hzaz.base.BASE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {








    public static boolean saveToLocal(Bitmap bmp, String bitName) throws IOException {
        return saveToLocal(bmp, BASE.getBaseDir(), bitName);
    }

    public static boolean saveToLocal(Bitmap bmp, String path, String bitName) throws IOException {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(bitName)) {
            LOG.e("BitmapUtil", "Input error");
            return false;
        }
        LOG.e("BitmapUtil", "saveToLocal.17:" + path);
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        if (!path.endsWith("/")) {
            path += "/";
        }
        String fileFullName = path + bitName + ".png";
        LOG.e("BitmapUtil", "saveToLocal.fileFullName:" + fileFullName);
        File f = new File(fileFullName);
        boolean flag = false;
        if(f.exists()){
            f.delete();
        }
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static String get64FromPath(String uploadPath) throws Exception {
        try {
            return Bitmap2StrByBase64(BitmapFactory.decodeFile(uploadPath), 80);
        } catch (OutOfMemoryError error) {
            try {
                return Bitmap2StrByBase64(BitmapFactory.decodeFile(uploadPath), 60);
            } catch (OutOfMemoryError error2) {
                // 设置参数
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
                BitmapFactory.decodeFile(uploadPath, options);
                int height = options.outHeight;
                int width = options.outWidth;
                int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
                int minLen = Math.min(height, width); // 原图的最小边长
                if (minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
                    float ratio = (float) minLen / 100.0f; // 计算像素压缩比例
                    inSampleSize = (int) ratio;
                }
                options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
                options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
                LOG.e("HttpParamUtil", "开始进行尺寸压缩，使用原尺寸  1/2");
                Bitmap bm = BitmapFactory.decodeFile(uploadPath, options); // 解码文件
                try {
                    return Bitmap2StrByBase64(bm, 60);
                } catch (OutOfMemoryError error13) {
                    inSampleSize = 3; // 默认像素压缩比例，压缩为原图的1/2
                    if (minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
                        float ratio = (float) minLen / 100.0f; // 计算像素压缩比例
                        inSampleSize = (int) ratio;
                    }
                    options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
                    options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
                    LOG.e("HttpParamUtil", "开始进行尺寸压缩，使用原尺寸  1/3");
                    try {
                        return Bitmap2StrByBase64(bm, 60);
                    } catch (Exception e) {
                        throw new Exception("图片过大或其他异常，请重新选择并尝试");
                    }
                }
            }
        }
    }

    public static String Bitmap2StrByBase64(Bitmap bit, int quality) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, quality, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}
