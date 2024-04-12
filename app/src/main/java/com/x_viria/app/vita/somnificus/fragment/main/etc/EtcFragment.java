package com.x_viria.app.vita.somnificus.fragment.main.etc;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.SettingActivity;

import java.util.Objects;

public class EtcFragment extends Fragment {

    private EtcViewModel mViewModel;

    public static EtcFragment newInstance() {
        return new EtcFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_etc, container, false);

        AdView AdView_LLIB1 = root.findViewById(R.id.EtcFragment__AdView_LLItemBanner_1);
        AdRequest AdRequest_LLIB1 = new AdRequest.Builder().build();
        AdView_LLIB1.loadAd(AdRequest_LLIB1);

        LinearLayout settings_btn = root.findViewById(R.id.EtcFragment__Btn_Settings);
        settings_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingActivity.class);
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