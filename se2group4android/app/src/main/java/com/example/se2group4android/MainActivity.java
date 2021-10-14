package com.example.se2group4android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView lightIv, fanIv, curtainIv, exitIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightIv = (ImageView) findViewById(R.id.lightImage);
        lightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLightActivity();
            }
        });
        fanIv = (ImageView) findViewById(R.id.fanImage);
        fanIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFanActivity();
            }
        });
        curtainIv = (ImageView) findViewById(R.id.curtainImage);
        curtainIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCurtainActivity();
            }
        });
    }
    public void openLightActivity(){
        Intent intent = new Intent(this, LightActivity.class);
        startActivity(intent);
    }
    public void openFanActivity(){
        Intent intent = new Intent(this, FanActivity.class);
        startActivity(intent);
    }
    public void openCurtainActivity(){
        Intent intent = new Intent(this, CurtainActivity.class);
        startActivity(intent);
    }
}
