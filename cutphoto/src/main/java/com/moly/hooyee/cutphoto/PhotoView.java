package com.moly.hooyee.cutphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Hooyee on 2016/12/16.
 * mail: hooyee01_moly@foxmail.com
 */

public class PhotoView extends ImageView implements View.OnTouchListener {

    static final int DOUBLE_CLICK_TIME_SPACE = 300; // 双击时间间隔

    private Matrix mOriginalMatrix;
    private Matrix mCurrentMatrix;
    private Matrix mMatrix;

    private float mStartY;
    private float mStartX;
    private long mLastTime;           // 第一次触摸屏幕的时间

    private Bitmap mBitmap;          // 记录Image的图片资源bitmap
    private RectF mBitmapRectF;
    private RectF mViewBoundRectF;
    private RectF mOriginalBitmapRectF;

    private float dX;
    private float dY;

    public PhotoView(Context context, AttributeSet attrs) {
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
            mBitmap = zoomBitmap(mBitmap, getWidth());
            mViewBoundRectF  = new RectF(0, 0, getWidth(), getHeight());
            mBitmapRectF = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            init(mBitmap);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
    }

    public void init(Bitmap bmp) {
        // bitmap修改符合view宽高之后重新设置为图片
        setImageBitmap(mBitmap);

        float width = bmp.getWidth();
        float height = bmp.getHeight();

        float dx = -(width - getWidth()) / 2;
        float dy = -(height - getHeight()) / 2;

        // 初始化bitmap的位置
        Matrix matrix = getImageMatrix();
        matrix.postTranslate(dx, dy);
        matrix.mapRect(mBitmapRectF);
        setImageMatrix(matrix);

        // 初始化边界矩阵
        mOriginalBitmapRectF = new RectF(mBitmapRectF);
        mOriginalMatrix = new Matrix();
        mOriginalMatrix.set(matrix);
    }

    /**
     * 将宽或者高缩放成view的宽高
     * @param bmp
     * @param measurement
     * @return
     */
    private Bitmap zoomBitmap(Bitmap bmp, float measurement) {

        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        int min = bmpWidth > bmpHeight ? bmpHeight : bmpWidth;

        Matrix matrix = new Matrix();
        matrix.setScale(measurement / min, measurement / min);
        Bitmap result = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
        return result;
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
        return true;
    }

    private void touchUp(MotionEvent event) {
        mMatrix.reset();
        // 一次滑动屏幕会多次触发move事件，而实际移动距离为最后一次事件获得的dX,dY
        mMatrix.postTranslate(dX, dY);
        mMatrix.mapRect(mBitmapRectF);
        dX = 0;
        dY = 0;
    }

    private void touchMove(MotionEvent event) {
        // 手指一次滑动会触发多次move事件：
        // 例如从x移动100，可能会多次触发：x = [10,15,29,50````100]等一组数据
        // 但是dY 确实以down时的数据为基准的  因此，一次滑动dY = 10+15+29+50+100；dy会出现前述的一组数据，因此实际的dy就被放大了,故mBitmapRectF不能在此处操作。
        dY = event.getY() - mStartY;
        dX = event.getX() - mStartX;

        // check up the bound
        if (mBitmapRectF.left + dX > mViewBoundRectF.left) {
            dX = mViewBoundRectF.left - mBitmapRectF.left;
        }
        if (mBitmapRectF.right + dX < mViewBoundRectF.right) {
            dX = mViewBoundRectF.right - mBitmapRectF.right;
        }
        if (mBitmapRectF.top + dY > mViewBoundRectF.top) {
            dY = mViewBoundRectF.top - mBitmapRectF.top;
        }
        if (mBitmapRectF.bottom + dY < mViewBoundRectF.bottom) {
            dY = mViewBoundRectF.bottom - mBitmapRectF.bottom;
        }

        mMatrix.set(mCurrentMatrix);
        mMatrix.postTranslate(dX, dY);
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

    // 当前放大次数
    private int mCurrentCount;
    // 每次执行放大操作时，放大的比例
    public static final float SCALE = 1.5f;
    public static final int MAX_COUNT = 2;

    private void changeSize(float x, float y) {
        Toast.makeText(getContext(), "down", Toast.LENGTH_SHORT).show();
        mMatrix.set(mCurrentMatrix);
        mCurrentCount++;
        if (mCurrentCount > MAX_COUNT) {
            reset();
        } else {
            float dx = - x * (SCALE - 1);
            float dy = - y * (SCALE - 1);

            mMatrix.postScale(SCALE, SCALE);
            // 以点击的位置（x, y）为重心放大，故需要移动dx，dy的量
            mMatrix.postTranslate(dx, dy);
            setImageMatrix(mMatrix);

            // 进来之前的矩阵移动量在其他地方(touchMove)已经操作在mBitmapRectF上了，
            // 但是image是直接设置matrix进去的，所以要沿用之前的mCurrentMatrix。
            mMatrix.reset();
            mMatrix.postScale(SCALE, SCALE);
            mMatrix.postTranslate(dx, dy);
            mMatrix.mapRect(mBitmapRectF);
        }
    }

    private void reset() {
        mCurrentCount = 0;
        setImageMatrix(mOriginalMatrix);
        mBitmapRectF.set(mOriginalBitmapRectF);
    }

}
