//package com.example.facerecognitionlock;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.PixelFormat;
//import android.os.Bundle;
//import android.view.Window;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import java.util.Random;
//
//public class SplashActivity extends Activity {
//    public void onAttachedToWindow(){
//        super.onAttachedToWindow();
//        Window window=getWindow();
//        window.setFormat(PixelFormat.RGBA_8888);
//    }
//    Thread splashThread;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//        StartAnimation();
//    }
//
//    public void StartAnimation(){
//        Animation anim= AnimationUtils.loadAnimation(this,R.anim.anim_splash_ball);
//        anim.reset();
//        ConstraintLayout c= (ConstraintLayout)findViewById(R.id.constraintLayout);
//        c.clearAnimation();
//        c.startAnimation(anim);
//
//        splashThread = new Thread(){
//            public void run(){
//                try{
//                    int waited=0;
//                    while(waited<3500){
//                        sleep(100);
//                        waited+=100;
//                    }
//                    Intent intent=new Intent(SplashActivity.this,Login.this);
//
//                }catch (InterruptedException e){
//
//                }
//            }
//        };
//        splashThread.start();
//
//    }
//
//}