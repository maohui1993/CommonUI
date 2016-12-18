package com.moly.hooyee.cutphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Hooyee on 2016/12/16.
 * mail: hooyee01_moly@foxmail.com
 */

public class PhotoHandle extends ImageView implements View.OnTouchListener {

    static final int DOUBLE_CLICK_TIME_SPACE = 500; // 双击时间间隔

    private Matrix mCurrentMatrix;
    private Matrix mMatrix;

    private float mStartY;
    private float mStartX;
    private float mMaxMoveValue;
    private float mMaxUpMoveValue;    // up or left
    private float mMaxDownMoveValue;  // down or right
    private float mCurrentScala;      // 当前放大倍数

    // 0代表水平，1代表垂直，-1不能滑动
    private int mOrientation;
    private long mLastTime;           // 第一次触摸屏幕的时间

    private Bitmap mBitmap;

    public PhotoHandle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCurrentMatrix = new Matrix();
        mMatrix = new Matrix();
        setOnTouchListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 在onCreate中获取宽度
        if (hasFocus) {
            Bitmap bitmap = zoomBitmap(mBitmap, getWidth());
            render(bitmap);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
    }

    public void render(Bitmap bmp) {
        setImageBitmap(bmp);
        float width = bmp.getWidth();
        float height = bmp.getHeight();
        float maxMoveValue;

        float dx = 0;
        float dy = 0;
        // 0代表水平，1代表垂直
        int orientation = 0;
        if (width < height) {
            maxMoveValue = (height - getHeight()) / 2;
            orientation = 1;
            dy = -maxMoveValue;
        } else if (width > height) {
            maxMoveValue = (width - getWidth()) / 2;
            orientation = 0;
            dx = -maxMoveValue;
        } else {
            maxMoveValue = 0;
            orientation = -1;
        }
        setMaxMoveValue(maxMoveValue);
        setOrientation(orientation);

        Matrix matrix = getImageMatrix();
        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);
    }

    private Bitmap zoomBitmap(Bitmap bmp, float measurement) {

        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        int min = bmpWidth > bmpHeight ? bmpHeight : bmpWidth;

        Matrix matrix = new Matrix();
        matrix.setScale(measurement/min, measurement/min);
        Bitmap result = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
        return result;
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setMaxMoveValue(float maxMoveValue) {
        mMaxMoveValue = maxMoveValue;
        mMaxUpMoveValue = maxMoveValue;
        mMaxDownMoveValue = maxMoveValue;
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
        drawBorder(canvas);
    }

    /**
     * 画image的边框
     * @param canvas
     */
    private void drawBorder(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(0, getMeasuredWidth());
        path.lineTo(getMeasuredWidth(), getMeasuredHeight());
        path.lineTo(getMeasuredWidth(), 0);
        path.lineTo(getMeasuredHeight(), 0);
        path.close();

        Paint p = new Paint();
        p.setColor(Color.BLUE);
        p.setStrokeWidth(6);
        p.setStyle(Paint.Style.STROKE);//设置为空心

        canvas.drawPath(path, p);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView image = (ImageView) v;

        if (mOrientation == -1) {
            return true;
        }

        switch (event.getAction() & 0xff) {
            // 一次滑动只会出发一次ACTION_DOWN
            case MotionEvent.ACTION_DOWN :
                mStartY = event.getY();
                mStartX = event.getX();
                mCurrentMatrix.set(image.getImageMatrix());
                if(event.getPointerCount() == 1) {
                    if (event.getEventTime() - mLastTime < DOUBLE_CLICK_TIME_SPACE) {

                    }
                }
                mLastTime = event.getEventTime();
                break;
            case MotionEvent.ACTION_MOVE :
                float moveAmountY = event.getY() - mStartY;
                float moveAmountX = event.getX() - mStartX;
                float moveAmount = mOrientation == 0 ? moveAmountX : moveAmountY;

                if (moveAmount < -mMaxUpMoveValue) {
                    moveAmount = -mMaxUpMoveValue;
                } else if (moveAmount > mMaxDownMoveValue) {
                    moveAmount = mMaxDownMoveValue;
                }

                mMatrix.set(mCurrentMatrix);
                if (mOrientation == 1) {
                    mMatrix.postTranslate(0, moveAmount);
                } else {
                    mMatrix.postTranslate(moveAmount, 0);
                }
                break;
            // 松开手指时，记录当前能移动的最大值
            case MotionEvent.ACTION_UP :
                float changeValue;
                if (mOrientation == 0) {
                    changeValue = event.getX() - mStartX;
                } else {
                    changeValue = event.getY() - mStartY;
                }
                if (changeValue > -mMaxUpMoveValue && changeValue < mMaxDownMoveValue) {
                    mMaxDownMoveValue -= changeValue;
                    mMaxUpMoveValue += changeValue;
                } else if (changeValue < -mMaxUpMoveValue){
                    mMaxDownMoveValue = 2 * mMaxMoveValue;
                    mMaxUpMoveValue = 0;
                } else if (changeValue > mMaxDownMoveValue){
                    mMaxDownMoveValue = 0;
                    mMaxUpMoveValue = 2 * mMaxMoveValue;
                }
                break;
        }

        image.setImageMatrix(mMatrix);
        return true;
    }


}
