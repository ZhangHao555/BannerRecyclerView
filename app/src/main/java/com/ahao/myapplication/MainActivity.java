package com.ahao.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ahao.myapplication.banner.BannerRecyclerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.legacy).setOnClickListener(e ->{
            Intent intent = new Intent(MainActivity.this,BannerRecyclerActivity.class);
            startActivity(intent);

        });

        findViewById(R.id.newer).setOnClickListener(e ->{
            Intent intent = new Intent(MainActivity.this,BannerViewActivity.class);
            startActivity(intent);

        });
    }
}
