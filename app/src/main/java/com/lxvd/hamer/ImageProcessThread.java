package com.lxvd.hamer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;

public class ImageProcessThread extends HandlerThread {

    private static final int MESSAGE_CONVERT = 0;

    private static final int PERCENT = 100;

    private static final int PARTS_COUNT = 50;

    private static final int PART_SIZE = PERCENT / PARTS_COUNT;



    private Handler mMainHandler;

    private Handler mBackgroundHandler;

    private Callback mCallback;



    public ImageProcessThread(String name) {

        super(name);

    }



    public void setCallback(Callback callback) {

        mCallback = callback;

    }



    @SuppressLint("HandlerLeak")

    @Override

    protected void onLooperPrepared() {

        mMainHandler = new Handler(Looper.getMainLooper());



        mBackgroundHandler = new Handler() {


            @Override

            public void handleMessage(Message msg) {

                switch (msg.what) {

                    case MESSAGE_CONVERT: {



                        Bitmap bitmap = (Bitmap) msg.obj;

                        processBitmap(bitmap);

                        msg.recycle();

                    }

                }

            }

        };

    }



    private void processBitmap(final Bitmap bitmap) {



        int h = bitmap.getHeight();

        int w = bitmap.getWidth();

        int[] pixels = new int[h * w];

        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);



        final int[] progress = new int[1];




        for (int i = 0; i < h * w; i++) {

            String hex = String.format("#%06X", (0xFFFFFF & pixels[i]));

            String R = hex.substring(1, 3);

            String G = hex.substring(3, 5);

            String B = hex.substring(5);

            String mess = B + R + G;

            pixels[i] = Integer.parseInt(mess, 16);

            int part = w * h / PARTS_COUNT;



            if (i % part == 0) {

                progress[0] = i / part * PART_SIZE; // <- костыльная магия


                mMainHandler.post(new Runnable() {

                    @Override

                    public void run() {



                        mCallback.sendProgress(progress[0]);

                    }

                });

            }

        }

        final Bitmap result = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.RGB_565);


        mMainHandler.post(new Runnable() {

            @Override

            public void run() {



                mCallback.onCompleted(result);

            }

        });



    }


    public void performOperation(Bitmap inputData) {



        mBackgroundHandler

                .obtainMessage(MESSAGE_CONVERT, inputData)

                .sendToTarget();

    }




    public interface Callback {

        void sendProgress(int progress);

        void onCompleted(Bitmap bitmap);

    }

}