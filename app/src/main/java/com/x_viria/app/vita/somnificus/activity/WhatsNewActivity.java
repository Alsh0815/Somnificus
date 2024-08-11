package com.x_viria.app.vita.somnificus.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.x_viria.app.vita.somnificus.R;

public class WhatsNewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_new);

        ImageView IV_Btn__UPD_OpenClose = findViewById(R.id.WhatsNewActivity__UPD_OpenClose);
        IV_Btn__UPD_OpenClose.setOnClickListener(v -> {
            LinearLayout LL__UPD = findViewById(R.id.WhatsNewActivity__UPD);
            int visibility = LL__UPD.getVisibility();
            switch (visibility) {
                case GONE:
                    IV_Btn__UPD_OpenClose.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_up));
                    LL__UPD.setVisibility(VISIBLE);
                    break;
                case VISIBLE:
                    IV_Btn__UPD_OpenClose.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_down));
                    LL__UPD.setVisibility(GONE);
                    break;
            }
        });
    }
}