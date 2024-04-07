package com.x_viria.app.vita.somnificus.fragment.tutorial;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.x_viria.app.vita.somnificus.R;

public class Tutorial1Fragment extends Fragment {

    private Tutorial1ViewModel mViewModel;

    public static Tutorial1Fragment newInstance() {
        return new Tutorial1Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial_1, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(Tutorial1ViewModel.class);
        // TODO: Use the ViewModel
    }

}