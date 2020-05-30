package com.rq.ctr.common_util.yzCode;

import android.util.Log;

import com.google.zxing.common.BitMatrix;

public class CodeBitMatrixUtil {

    int bConnerLength = 0;//三个大角连续长度
    int width = 0;  //原版最小单元长度
    int LTX = -1;//左上点位置
    int LTY = -1;//左上点位置
    int RTX = -1;//右上点位置
    int RTY = -1;//右上点位置
    int LBX = -1;//左下点位置
    int LBY = -1;//左下点位置
    private float showRate = 0.4F;

    public CodeBitMatrixUtil(BitMatrix bitMatrix, int width, int height) {
        int cHeight = 0;//三个大角连续长度
        int cWidth = 0;
        int LTX = -1;//左上点位置
        int LTY = -1;//左上点位置
        int maxX = 0;
        int maxY = 0;
        boolean hasMaxWidth = false;//未找到三大角尺寸
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {//内容区
                    if (LTX < 0) {
                        LTX = x - 1;
                        LTY = y - 1;
                    }
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);

                    if (!hasMaxWidth) {
                        cHeight++;
                    }
                } else {
                    if (cHeight != 0) {
                        hasMaxWidth = true;
                    }
                    if (LTY > 0 && x > LTY && x < cHeight && cWidth == 0) {
                        cWidth = y - LTX;
                    }
                }
            }
        }
        cHeight++;
        cWidth++;
        this.bConnerLength = cHeight;
        this.width = cWidth - 1;
        this.LTX = LTX;
        this.LTY = LTY;
        this.RTX = maxX + 1;
        this.RTY = LTY;
        this.LBX = LTX;
        this.LBY = maxY + 1;
        String res = "LTX = " + LTX + ",LTY = " + LTY + ",RTX = " + RTX + ",RTY = " + RTY + ",LBX = " + LBX + ",LBY = " + LBY;


        Log.e("QCode", cHeight + "<>" + cWidth + "\n" + res);
    }

    public boolean isBigBlack(int x, int y) {
        if (x > LTX && x < LTX + bConnerLength && y > LTY && y < LTY + bConnerLength) {
            //左上角
            return true;
        }
        if (x > RTX - bConnerLength && x < RTX && y > RTY && y < RTY + bConnerLength) {
            //右上角
            return true;
        }
        if (x > LBX && x < LBX + bConnerLength && y > LBY - bConnerLength && y < LBY) {
            //左下角
            Log.e("CodeBitMatrixUtil", "LINE:75");
            return true;
        }
        return isSmallConner(x, y);
    }

    private boolean isSmallConner(int x, int y) {//小角
        int common = (int) (0.9F * width);
        int sX = RTX - bConnerLength - 2 * common;
        int sy = LBY - bConnerLength - 2 * common;
        int eX = RTX - bConnerLength + 3 * common + 1;
        int ey = LBY - bConnerLength + 3 * common + 1;
        if (x > sX && x < eX && y > sy && y < ey) {
            return true;
        }
        return isLine(x, y);
    }

    private boolean isLine(int x, int y) {//黑色线条
        int mixX = LTX + bConnerLength - width;
        int mixY = LTY + bConnerLength - width;
        int maxX = LTX + bConnerLength;
        int maxY = LTY + bConnerLength;
        return x > mixX && x < maxX || y > mixY && y < maxY;
    }

    public boolean black2White(int x, int y) {
        float rWidth = width - 1;
        float rX = x - LTX;
        float rY = y - LTY;
        if (rX % rWidth / rWidth < .5f - showRate / 2) {
            return true;
        }
        if (rX % rWidth / rWidth > .5f + showRate / 2) {
            return true;
        }
        if (rY % rWidth / rWidth < .5f - showRate / 2) {
            return true;
        }
        if (rY % rWidth / rWidth > .5f + showRate / 2) {
            return true;
        }
        return false;
    }
}
