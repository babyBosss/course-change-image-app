package com.lxvd.hamer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements ImageProcessThread.Callback{



    private ImageView mDoge;

    private ProgressBar mProgressBar;

    private ImageProcessThread mImageProcessThread;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Button performBtn = findViewById(R.id.btn_perform);

        mDoge = findViewById(R.id.iv_doge);

        mProgressBar = findViewById(R.id.progress);



        mImageProcessThread = new ImageProcessThread("Background ");


        mImageProcessThread.start();

        mImageProcessThread.getLooper();

        mImageProcessThread.setCallback(this);


        performBtn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {


                BitmapDrawable drawable = (BitmapDrawable) mDoge.getDrawable();

                mImageProcessThread.performOperation(drawable.getBitmap());

            }

        });

    }


    @Override

    public void sendProgress(int progress) {

        mProgressBar.setProgress(progress);

    }

    @Override

    public void onCompleted(Bitmap bitmap) {

        mDoge.setImageBitmap(bitmap);

    }

    @Override

    protected void onDestroy() {

        mImageProcessThread.quit();

        super.onDestroy();

    }

}
