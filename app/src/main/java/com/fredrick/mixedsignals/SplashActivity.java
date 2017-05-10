package com.fredrick.mixedsignals;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fredrick.mixedsignals.preference.SessionStore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            public void run(){
                int iKey = SessionStore.restoreInt(getApplicationContext(), "Launched");
                if (iKey > 0) {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent instructionIntent = new Intent(SplashActivity.this, InstructionActivity.class);
                    instructionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(instructionIntent);
                    finish();
                }
            }
        }, 1500);
    }
}
