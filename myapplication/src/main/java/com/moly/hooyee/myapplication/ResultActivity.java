package com.moly.hooyee.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.moly.hooyee.cutphoto.CoverView;
import com.moly.hooyee.cutphoto.PhotoPicker;
import com.moly.hooyee.cutphoto.PhotoView;

public class ResultActivity extends Activity {

    private PhotoView photoView;
    private CoverView coverView;
    private Bitmap mBitmap;
    private Bitmap b;
    private Bitmap[][] mDstBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String picPath = intent.getStringExtra("mPicPath");
        mBitmap = BitmapFactory.decodeFile(picPath);
        photoView = (PhotoView) findViewById(R.id.photo);
        photoView.setImageBitmap(mBitmap);
        coverView = (CoverView) findViewById(R.id.cover);

        Button bt1 = (Button) findViewById(R.id.bt_photo_cut_1);
        Button bt2 = (Button) findViewById(R.id.bt_photo_cut_2);
        Button bt3 = (Button) findViewById(R.id.bt_photo_cut_3);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoView.setDrawingCacheEnabled(true);
                photoView.buildDrawingCache();
                b = Bitmap.createBitmap(photoView.getDrawingCache());
                photoView.setDrawingCacheEnabled(false);

                mDstBitmaps = PhotoPicker.cutPhoto(b, 1);
            }
        });
    }

}
