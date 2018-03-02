package com.example.saiprasadgarimella.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                } finally{
                    Intent i=new Intent(SplashActivity.this, MainActivity.class);

                    startActivity(i);

                    finish();
                }
            }
        };
        timerThread.start();
    }
}
