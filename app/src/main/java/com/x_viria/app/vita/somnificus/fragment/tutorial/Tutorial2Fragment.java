package com.x_viria.app.vita.somnificus.fragment.tutorial;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.x_viria.app.vita.somnificus.R;

import java.util.Objects;

public class Tutorial2Fragment extends Fragment {

    private View ROOT;
    private Tutorial2ViewModel mViewModel;

    public static Tutorial2Fragment newInstance() {
        return new Tutorial2Fragment();
    }

    private void refreshUI() {
        Button Btn_Allow_Notification = ROOT.findViewById(R.id.Tutorial2Fragment__Btn_Allow_Notifications);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            Btn_Allow_Notification.setVisibility(View.GONE);
        } if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Btn_Allow_Notification.setEnabled(false);
        }
        Button Btn_Allow_Overlay = ROOT.findViewById(R.id.Tutorial2Fragment__Btn_Allow_Overlays);
        if (Settings.canDrawOverlays(getContext())) Btn_Allow_Overlay.setEnabled(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ROOT = inflater.inflate(R.layout.fragment_tutorial_2, container, false);

        Button Btn_Allow_Notification = ROOT.findViewById(R.id.Tutorial2Fragment__Btn_Allow_Notifications);
        Btn_Allow_Notification.setOnClickListener(v -> {
            String[] permissions = {
                    Manifest.permission.POST_NOTIFICATIONS
            };
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0xF00);
        });

        Button Btn_Allow_Overlay = ROOT.findViewById(R.id.Tutorial2Fragment__Btn_Allow_Overlays);
        Btn_Allow_Overlay.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().getPackageName()));
            startActivityForResult(intent, 0xfff);
        });

        refreshUI();

        return ROOT;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(Tutorial2ViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0xF00) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Granted Permission: " + permissions[i], Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Rejected Permission: " + permissions[i], Toast.LENGTH_SHORT).show();
                }
            }
        }
        refreshUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

}