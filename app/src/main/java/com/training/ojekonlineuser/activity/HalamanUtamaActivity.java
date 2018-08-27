package com.training.ojekonlineuser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.training.ojekonlineuser.R;

public class HalamanUtamaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);
    }

    public void onGoride(View view) {
        startActivity(new Intent(this,MapsActivity.class));
    }

    public void onHistory(View view) {
    }
}
