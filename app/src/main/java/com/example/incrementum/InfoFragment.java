package com.example.incrementum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InfoFragment extends DialogFragment {

    private static final String ARG_COLOR = "color";

    public static InfoFragment newInstance(String color) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLOR, color);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        TextView infoText = view.findViewById(R.id.description_text);

        if (getArguments() != null) {
            infoText.setText(getArguments().getString(ARG_COLOR, "Unknown"));
        }

        view.setOnClickListener(v -> dismiss()); // Закрытие по нажатию
        return view;
    }
}
