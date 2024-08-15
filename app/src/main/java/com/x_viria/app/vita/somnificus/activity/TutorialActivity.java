package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.fragment.tutorial.Tutorial1Fragment;
import com.x_viria.app.vita.somnificus.fragment.tutorial.Tutorial2Fragment;
import com.x_viria.app.vita.somnificus.fragment.tutorial.TutorialLastFragment;

public class TutorialActivity extends AppCompatActivity {

    private final Fragment[] FRAGMENT = {
            new Tutorial1Fragment(),
            new Tutorial2Fragment(),
            new TutorialLastFragment()
    };
    private int CURRENT_PAGE = 1;

    private void refreshControllPanel() {
        int iconTintColor = ContextCompat.getColor(this, R.color.primaryBackgroundColor);
        int iconTintColorReverse = ContextCompat.getColor(this, R.color.primaryActiveColor);
        TextView tv_page = findViewById(R.id.TutorialActivity__CP_Text_Page);
        tv_page.setText(String.format("%d / %d", CURRENT_PAGE, FRAGMENT.length));

        LinearLayout back_btn = findViewById(R.id.TutorialActivity__CP_Btn_Back);
        back_btn.setVisibility(View.VISIBLE);
        LinearLayout next_btn = findViewById(R.id.TutorialActivity__CP_Btn_Next);
        next_btn.setVisibility(View.VISIBLE);
        ImageView next_btn_icon = findViewById(R.id.TutorialActivity__CP_Btn_Next_Icon);
        if (CURRENT_PAGE == 1) back_btn.setVisibility(View.INVISIBLE);
        if (CURRENT_PAGE == FRAGMENT.length) next_btn.setVisibility(View.INVISIBLE);
        if (
            CURRENT_PAGE == 2
            && (
                !Settings.canDrawOverlays(this)
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            )
        ) {
            next_btn.setClickable(false);
            next_btn.setBackground(getDrawable(R.drawable.btn_timer_circle_reverse));
            next_btn.setBackgroundTintList(ColorStateList.valueOf(iconTintColor));
            next_btn_icon.setImageTintList(ColorStateList.valueOf(iconTintColorReverse));
        } else {
            next_btn.setClickable(true);
            next_btn.setBackground(getDrawable(R.drawable.btn_timer_circle));
            next_btn.setBackgroundTintList(ColorStateList.valueOf(iconTintColorReverse));
            next_btn_icon.setImageTintList(ColorStateList.valueOf(iconTintColor));
        }
    }

    private void setFragment(Fragment fragment) {
        // Set a fragment.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.setCustomAnimations(R.anim.fragment_enter_to_right, R.anim.fragment_exit_to_left);
        transaction.replace(R.id.TutorialActivity__FragmentContainer, fragment);
        transaction.commitAllowingStateLoss();

        // Fix the bottom margin of the fragment.
        int bnv_height = findViewById(R.id.TutorialActivity__ControlPanel).getHeight();
        View fragment_container = findViewById(R.id.TutorialActivity__FragmentContainer);
        ViewGroup.LayoutParams lp = fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
        mlp.setMargins(mlp.leftMargin, mlp.topMargin, mlp.rightMargin, bnv_height);
        fragment_container.setLayoutParams(mlp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        setFragment(FRAGMENT[0]);
        refreshControllPanel();

        LinearLayout back_btn = findViewById(R.id.TutorialActivity__CP_Btn_Back);
        back_btn.setOnClickListener(v -> {
            if (1 < CURRENT_PAGE) {
                getSupportFragmentManager().popBackStack();
                CURRENT_PAGE--;
                refreshControllPanel();
            }
        });
        LinearLayout next_btn = findViewById(R.id.TutorialActivity__CP_Btn_Next);
        next_btn.setOnClickListener(v -> {
            int idx = CURRENT_PAGE - 1;
            if (CURRENT_PAGE < FRAGMENT.length) {
                setFragment(FRAGMENT[idx + 1]);
                CURRENT_PAGE++;
                refreshControllPanel();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshControllPanel();
    }

}