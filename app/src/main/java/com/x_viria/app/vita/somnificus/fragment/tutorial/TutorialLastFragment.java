package com.x_viria.app.vita.somnificus.fragment.tutorial;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.MainActivity;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import java.util.Objects;

public class TutorialLastFragment extends Fragment {

    private TutorialLastViewModel mViewModel;

    public static TutorialLastFragment newInstance() {
        return new TutorialLastFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tutorial_last, container, false);

        Button start_btn = root.findViewById(R.id.TutorialLastFragment__Btn_Start_MainActivity);
        start_btn.setOnClickListener(v -> {
            SPStorage.Tutorial.setCompletedFlag(requireContext());
            requireActivity().finish();
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TutorialLastViewModel.class);
        // TODO: Use the ViewModel
    }

}