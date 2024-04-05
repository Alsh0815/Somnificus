package com.x_viria.app.vita.somnificus.fragment.alarm;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.SetAlarmActivity;
import com.x_viria.app.vita.somnificus.core.Alarm;

public class AlarmFragment extends Fragment {

    private AlarmViewModel mViewModel;

    public static AlarmFragment newInstance() {
        return new AlarmFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_alarm, container, false);

        FloatingActionButton fab = root.findViewById(R.id.AlarmFragment__FloatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SetAlarmActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        // TODO: Use the ViewModel
    }

}