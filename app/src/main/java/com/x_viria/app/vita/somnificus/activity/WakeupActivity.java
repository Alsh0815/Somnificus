package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.service.PlaySoundService;

public class WakeupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = new Intent(this, PlaySoundService.class);
        LinearLayout stopSound = findViewById(R.id.WakeupActivity__Stop_Sound_Btn);
        stopSound.setOnClickListener(v -> {
            stopService(intent);
            finish();
        });
    }

}