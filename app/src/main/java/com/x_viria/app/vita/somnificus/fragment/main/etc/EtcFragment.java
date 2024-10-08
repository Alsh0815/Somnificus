package com.x_viria.app.vita.somnificus.fragment.main.etc;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.BackupActivity;
import com.x_viria.app.vita.somnificus.activity.CreditsActivity;
import com.x_viria.app.vita.somnificus.activity.PremiumActivity;
import com.x_viria.app.vita.somnificus.activity.SettingActivity;
import com.x_viria.app.vita.somnificus.activity.WhatsNewActivity;

public class EtcFragment extends Fragment {

    private EtcViewModel mViewModel;

    public static EtcFragment newInstance() {
        return new EtcFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_etc, container, false);

        LinearLayout whatsnew_btn = root.findViewById(R.id.EtcFragment__Btn_WhatsNew);
        whatsnew_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), WhatsNewActivity.class);
            startActivity(intent);
        });

        LinearLayout premium_btn = root.findViewById(R.id.EtcFragment__Btn_Premium);
        premium_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PremiumActivity.class);
            startActivity(intent);
        });

        LinearLayout settings_btn = root.findViewById(R.id.EtcFragment__Btn_Settings);
        settings_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingActivity.class);
            startActivity(intent);
        });

        LinearLayout backup_btn = root.findViewById(R.id.EtcFragment__Btn_Backup);
        backup_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BackupActivity.class);
            startActivity(intent);
        });

        LinearLayout credits_btn = root.findViewById(R.id.EtcFragment__Btn_Credits);
        credits_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreditsActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EtcViewModel.class);
        // TODO: Use the ViewModel
    }

}