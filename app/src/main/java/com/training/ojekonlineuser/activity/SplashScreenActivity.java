package com.training.ojekonlineuser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.training.ojekonlineuser.MainActivity;
import com.training.ojekonlineuser.R;
import com.training.ojekonlineuser.helper.SessionManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final SessionManager sessionManager  = new SessionManager(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //jika ada data di sharedpreference
                if (sessionManager.isLogin()){
                    startActivity(new Intent(SplashScreenActivity.this,HalamanUtamaActivity.class));
                finish();
                }else{
                    //jika tidak ada data di sharedpreference
                    startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
                    finish();

                }
            }
        },5000);
    }
}
