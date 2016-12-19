package com.moly.hooyee.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.moly.hooyee.cutphoto.PhotoHandle;

public class ResultActivity extends Activity {

    private PhotoHandle photoView;
    private Bitmap mBitmap;
    Bitmap b;
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



        Button bt = (Button) findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoView.setDrawingCacheEnabled(true);
                photoView.buildDrawingCache();
                b = Bitmap.createBitmap(photoView.getDrawingCache());
                photoView.setDrawingCacheEnabled(false);
                ImageView p1 = (ImageView) findViewById(R.id.photo1);
                p1.setImageBitmap(b);
                Toast.makeText(ResultActivity.this, "h " + b.getHeight() + ", w " + b.getWidth(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
