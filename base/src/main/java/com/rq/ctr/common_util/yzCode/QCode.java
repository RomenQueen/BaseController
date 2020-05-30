package com.rq.ctr.common_util.yzCode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.rq.ctr.BASE;
import com.rq.ctr.common_util.LOG;

import java.io.InputStream;
import java.util.HashMap;

public class QCode {

    public Bitmap getCode(String content, int resId) {
        // TODO: 2019/9/30 找时间解耦 CodeBitMatrixUtil 的写法,注意 X Y 的颠倒
        return getCodeBitmap(content, 800, 800, getBitmapFromRes(BASE.getCxt(), resId));
    }

    public static Bitmap getBitmapFromRes(Context context, int resId) {
        Resources r = context.getResources();
        //以数据流的方式读取资源
        InputStream is = r.openRawResource(resId);
        BitmapDrawable bmpDraw = new BitmapDrawable(r, is);
        return bmpDraw.getBitmap();
    }


    private static Bitmap getCodeBitmap(String contents, int width, int height, Bitmap bmp) {
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");    //指定字符编码为“utf-8”
        LOG.e("QCode", contents + "\ngetCodeBitmap.38:" + ErrorCorrectionLevel.Q);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);  //二维码纠错等级
        hints.put(EncodeHintType.MARGIN, 1);    //设置图片的边距
        QRCodeWriter qrCodeWriter = new QRCodeWriter();//获得一个写二维码的对象
        try {
            //定义一个矩阵,接收生成的二维码,这里根据传进来的宽(width)高(height)和内容(contents)来生成二维码
            BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
            //定义一个大小为二维码宽高的数组，画出其中每一位的颜色（画二维码）
            int[] arr = new int[width * height];
            new CodeBitMatrixUtil(bitMatrix, width, height);

//            int jiaoL = 0;//三个大角连续长度
//            int jiaoK = 0;
//            int firstX = -1;//第一个点位置
//            int firstY = -1;//第一个点位置
            int centerStartX = -1;
            int centerStartY = -1;
            int centerEndX = -1;
            int centerEndY = -1;

            if (bmp != null) {
                centerStartX = width / 2 - bmp.getWidth() / 2;
                centerEndX = centerStartX + bmp.getWidth();
                centerStartY = height / 2 - bmp.getHeight() / 2;
                centerEndY = centerStartY + bmp.getHeight();
            }

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    boolean needBitmap = x > centerStartX && x < centerEndX && y > centerStartY && y < centerEndY;
                    if (needBitmap) {
                        arr[y * width + x] = bmp.getPixel(x - centerStartX, y - centerStartY);
                    } else if (bitMatrix.get(x, y)) {//内容区
//                        if (util.isBigBlack(y,x)) {
                        arr[y * width + x] = 0x00000000;
//                        } else if (util.black2White(y, x)) {
//                            arr[y * width + x] = 0xffffffff;
//                        } else {
//                            arr[y * width + x] = 0x007b7b7b;
//                        }
                    } else {
                        arr[y * width + x] = 0xffffffff;
                    }
                }
            }
            //使用Bitmap的createBitmap方法将arr数组创建为一个位图
            return Bitmap.createBitmap(arr, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
