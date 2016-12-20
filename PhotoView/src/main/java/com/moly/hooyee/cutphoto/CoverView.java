package com.moly.hooyee.cutphoto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hooyee on 2016/12/20.
 * mail: hooyee01_moly@foxmail.com
 */

public class CoverView extends View {

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 在onCreate中获取宽度
        if (hasFocus) {

        }
    }

    public CoverView(Context context, AttributeSet set) {
        super(context, set);
    }

    //    EXACTLY：一般是设置了明确的值或者是MATCH_PARENT
    //    AT_MOST：表示子布局限制在一个最大值内，一般为WARP_CONTENT
    //    UNSPECIFIED：表示子布局想要多大就多大，很少使用
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            // 本 view的width还是需要填满 容器
            width = widthSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            // 宽和高保持一样
            height = width;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBash(canvas);
    }

    private int phase = 0;

    private void drawBash(Canvas canvas) {
        float x = getWidth() / 3;
        float y = getHeight() / 3;

        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(getWidth(), y);

        DashPathEffect dashEffect = new DashPathEffect(new float[]{5,15,5,15},phase);

        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStrokeWidth(6);
        p.setStyle(Paint.Style.STROKE);//设置为空心
        p.setPathEffect(dashEffect);

        canvas.drawPath(path, p);
        phase ++;
        invalidate();
    }
}
