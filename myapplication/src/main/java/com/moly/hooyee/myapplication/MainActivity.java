package com.moly.hooyee.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

/*
        ContentView content = new ContentView(this);
        View v = LayoutInflater.from(this).inflate(R.layout.activity_main1, null);
        content.addMainContent(v);
        content.setNavHeaderBackground(R.drawable.backgroud);
        setContentView(content.getContent());
*/
        photoTest();
    }

    public void photoTest() {
        setContentView(R.layout.activity_main1);
        Intent intent = new Intent(Intent.ACTION_PICK);
        //设置Data和Type属性，前者是URI：表示系统图库的URI,后者是MIME码
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
        //启动这个intent所指向的Activity
        startActivityForResult(intent, RESULT_IMAGE);
    }

    //设置返回码：标识本地图库
    private static final int RESULT_IMAGE = 100;
    //设置MIME码：表示image所有格式的文件均可
    private static final String IMAGE_TYPE = "image/*";

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     //   super.onActivityResult(requestCode, resultCode, data);
        Log.i("photo", "" + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_IMAGE && data != null) {
                //相册
                //通过获取当前应用的contentResolver对象来查询返回的data数据
                Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null, null);
                //将cursor指针移动到数据首行
                cursor.moveToFirst();
                //获取字段名为_data的数据
                String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                //设置一个intent
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                //传入所获取的图片的路径
                intent.putExtra("mPicPath", imagePath);
                //销毁cursor对象，释放资源
                cursor.close();
                startActivity(intent);
            }
        }
        finish();
    }
}
