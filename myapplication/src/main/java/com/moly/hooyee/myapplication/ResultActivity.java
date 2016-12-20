package com.moly.hooyee.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.moly.hooyee.cutphoto.PhotoPicker;
import com.moly.hooyee.cutphoto.PhotoView;

public class ResultActivity extends Activity {

    private PhotoView photoView;
    private Bitmap mBitmap;
    Bitmap b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String picPath = intent.getStringExtra("mPicPath");
        mBitmap = BitmapFactory.decodeFile(picPath);
        photoView = (PhotoView) findViewById(R.id.photo);
        photoView.setImageBitmap(mBitmap);

        Button bt = (Button) findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoView.setDrawingCacheEnabled(true);
                photoView.buildDrawingCache();
                b = Bitmap.createBitmap(photoView.getDrawingCache());
                photoView.setDrawingCacheEnabled(false);

                Bitmap[][] bmps = PhotoPicker.cutPhoto(b, 3);

                ImageView p1 = (ImageView) findViewById(R.id.photo1);
                ImageView p2 = (ImageView) findViewById(R.id.photo2);
                ImageView p3 = (ImageView) findViewById(R.id.photo3);
                ImageView p4 = (ImageView) findViewById(R.id.photo4);
                ImageView p5 = (ImageView) findViewById(R.id.photo5);
                ImageView p6 = (ImageView) findViewById(R.id.photo6);
//                ImageView p7 = (ImageView) findViewById(R.id.photo7);
//                ImageView p8 = (ImageView) findViewById(R.id.photo8);
//                ImageView p9 = (ImageView) findViewById(R.id.photo9);
                p1.setImageBitmap(bmps[0][0]);
                p2.setImageBitmap(bmps[0][1]);
                p3.setImageBitmap(bmps[0][2]);
                p4.setImageBitmap(bmps[1][0]);
                p5.setImageBitmap(bmps[1][1]);
                p6.setImageBitmap(bmps[1][2]);
//                p7.setImageBitmap(bmps[2][0]);
//                p8.setImageBitmap(bmps[2][1]);
//                p9.setImageBitmap(bmps[2][2]);
                Toast.makeText(ResultActivity.this, "h " + b.getHeight() + ", w " + b.getWidth(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
