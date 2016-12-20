package com.moly.hooyee.cutphoto;

import android.graphics.Bitmap;

/**
 * Created by Hooyee on 2016/12/20.
 * mail: hooyee01_moly@foxmail.com
 */

public class PhotoPicker {

    /**
     * 切割图片
     * @param srcBitmap 被切割的源文件
     * @param size 切割成多少份 [1 , 2, 3] <---> [1, 4, 9];
     * @return
     */
    public static Bitmap[][] cutPhoto(Bitmap srcBitmap, int size) {
        //
        if (size == 1) {
        }

        Bitmap[][] dstBitmap = new Bitmap[size][size];
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();

        int subWidth = srcWidth / size;
        int subHeight = srcHeight / size;

        int x;
        int y;
        for (int i = 0; i < size; i++) {
            x = srcWidth * i / size;
            for (int j = 0; j < size; j++) {
                y = srcHeight * j / size;
                dstBitmap[j][i] = Bitmap.createBitmap(srcBitmap, x, y, subWidth, subHeight);
            }
        }

        return dstBitmap;
    }
}
