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

    static final int DOUBLE_CLICK_TIME_SPACE = 300; // 双击时间间隔
    static final float MAX_SCALA = 1.5f;
    static final float TOLERANCE = 0.5f;

    private Matrix mCurrentMatrix;
    private Matrix mMatrix;

    private float mStartY;
    private float mStartX;
    private float mMaxMoveRecordX;     // 记录最大的移动值，不做改变
    private float mMaxMoveRecordY;     // 记录最大的移动值，不做改变
    private float mMaxMoveValueY;
    private float mMaxMoveValueX;
    private float mMaxUpMoveValue;    // up or left
    private float mMaxDownMoveValue;  // down or right
    private float mCurrentScala = 1f;      // 当前放大倍数

    // 0代表水平，1代表垂直，-1不能滑动
    private int mOrientation;
    private long mLastTime;           // 第一次触摸屏幕的时间

    private Bitmap mBitmap;

    public PhotoHandle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCurrentMatrix = new Matrix();
        mMatrix = new Matrix();
        setOnTouchListener(this);
        initType();
    }

    private void initType() {
        setScaleType(ScaleType.MATRIX);
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

    float w;
    float h;

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        w = bm.getWidth();
        h = bm.getHeight();
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
        setOrientation(orientation);
        setMaxMoveValue(maxMoveValue);

        Matrix matrix = getImageMatrix();
        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);
    }

    private Bitmap zoomBitmap(Bitmap bmp, float measurement) {

        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        int min = bmpWidth > bmpHeight ? bmpHeight : bmpWidth;

        Matrix matrix = new Matrix();
        matrix.setScale(measurement / min, measurement / min);
        Bitmap result = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
        return result;
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setMaxMoveValue(float maxMoveValue) {
        if (mOrientation == 1) {
            mMaxMoveRecordY = maxMoveValue;
            mMaxMoveValueY = maxMoveValue;
            mMaxUpMoveValue = maxMoveValue;
            mMaxDownMoveValue = maxMoveValue;
        } else if (mOrientation == 0) {
            mMaxMoveRecordX = maxMoveValue;
            mMaxMoveValueX = maxMoveValue;
            mMaxLeftMoveValue = maxMoveValue;
            mMaxRightMoveValue = maxMoveValue;
        }
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
     *
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

        if (mOrientation == -1) {
            return true;
        }

        switch (event.getAction() & 0xff) {
            // 一次滑动只会出发一次ACTION_DOWN
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            // 松开手指时，记录当前能移动的最大值
            case MotionEvent.ACTION_UP:
                touchUp(event);
                break;
        }

        //   image.setImageMatrix(mMatrix);
        return true;
    }

    private void touchUp(MotionEvent event) {
        float changeValueX = event.getX() - mStartX;
        float changeValueY = event.getY() - mStartY;

        if (changeValueY > -mMaxUpMoveValue && changeValueY < mMaxDownMoveValue) {
            mMaxDownMoveValue -= changeValueY;
            mMaxUpMoveValue += changeValueY;
        } else if (changeValueY < -mMaxUpMoveValue) {
            mMaxDownMoveValue = 2 * mMaxMoveValueY;
            mMaxUpMoveValue = 0;
        } else if (changeValueY > mMaxDownMoveValue) {
            mMaxDownMoveValue = 0;
            mMaxUpMoveValue = 2 * mMaxMoveValueY;
        }
        if (changeValueX > -mMaxLeftMoveValue && changeValueX < mMaxRightMoveValue) {
            mMaxRightMoveValue -= changeValueX;
            mMaxLeftMoveValue += changeValueX;
        } else if (changeValueX < -mMaxLeftMoveValue) {
            mMaxRightMoveValue = 2 * mMaxMoveValueX;     //
            mMaxLeftMoveValue = 0;
        } else if (changeValueX > mMaxRightMoveValue) {
            mMaxRightMoveValue = 0;
            mMaxLeftMoveValue = 2 * mMaxMoveValueX;    //
        }
    }

    float mMaxLeftMoveValue = 0;
    float mMaxRightMoveValue = 0;

    private void touchMove(MotionEvent event) {
        float moveAmountY = event.getY() - mStartY;
        float moveAmountX = event.getX() - mStartX;

        if (moveAmountY < -mMaxUpMoveValue) {
            moveAmountY = -mMaxUpMoveValue;
        } else if (moveAmountY > mMaxDownMoveValue) {
            moveAmountY = mMaxDownMoveValue;
        }

        if (moveAmountX < -mMaxLeftMoveValue) {
            moveAmountX = -mMaxLeftMoveValue;
        } else if (moveAmountX > mMaxRightMoveValue) {
            moveAmountX = mMaxRightMoveValue;
        }

        mMatrix.set(mCurrentMatrix);
        mMatrix.postTranslate(moveAmountX, moveAmountY);
        setImageMatrix(mMatrix);
    }

    private void touchDown(MotionEvent event) {
        mStartX = event.getX();
        mStartY = event.getY();
        if (event.getPointerCount() == 1) {
            if (event.getEventTime() - mLastTime < DOUBLE_CLICK_TIME_SPACE) {
                changeSize(mStartX, mStartY);
            }
        }
        mCurrentMatrix.set(getImageMatrix());
        mLastTime = event.getEventTime();
    }

    private void changeSize(float x, float y) {
        Matrix matrix = new Matrix();
        //    matrix.set(getImageMatrix());
        mCurrentScala += TOLERANCE;
        if (mCurrentScala > MAX_SCALA) {
            matrix.reset();
            mCurrentScala = 1;
        }
        matrix.postScale(mCurrentScala, mCurrentScala);
        mMaxMoveValueX = (w * mCurrentScala - getWidth()) / 2;
        mMaxMoveValueY = (h * mCurrentScala - getHeight()) / 2;

        // 记录平移钱的可以用的右滑和下滑的位移量
        float tmpX = mMaxRightMoveValue * mCurrentScala;
        float tmpY = mMaxDownMoveValue * mCurrentScala;

        mMaxDownMoveValue = 0;
        mMaxUpMoveValue = mMaxMoveValueY * 2;

        mMaxRightMoveValue = 0;
        mMaxLeftMoveValue = mMaxMoveValueX * 2;

        float dx ;
        float dy ;

        if (mCurrentScala == 1) {
            tmpX = 0;
            tmpY = y > mMaxUpMoveValue ? mMaxUpMoveValue : y;
        }
        dx = tmpX + x * (mCurrentScala - 1);
        dy = tmpY + y * (mCurrentScala - 1);

        matrix.postTranslate(-dx, -dy);

        mMaxDownMoveValue += dy;
        mMaxUpMoveValue -= dy;
        mMaxLeftMoveValue -= dx;
        mMaxRightMoveValue += dx;
        setImageMatrix(matrix);
    }

}
