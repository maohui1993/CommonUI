package com.moly.hooyee.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import com.moly.hooyee.cutphoto.PhotoHandle;

public class ResultActivity extends Activity {

    private PhotoHandle photoView;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String picPath = intent.getStringExtra("mPicPath");
        mBitmap = BitmapFactory.decodeFile(picPath);
        photoView = (PhotoHandle) findViewById(R.id.photo);
        photoView.setImageBitmap(mBitmap);
       // Bitmap bmp = zoomBitmap(measurement);
     //   photoView.render(mBitmap);
    }

    private Bitmap zoomBitmap(float measurement) {

        int bmpWidth = mBitmap.getWidth();
        int bmpHeight = mBitmap.getHeight();

        int min = bmpWidth > bmpHeight ? bmpHeight : bmpWidth;

        Matrix matrix = new Matrix();
        matrix.setScale(measurement/min, measurement/min);
        Bitmap result = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
        return result;
    }

}
