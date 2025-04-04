package com.example.incrementum;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CardInfoFragment extends DialogFragment {

    private static final String ARG_COLOR = "color";

    public static CardInfoFragment newInstance(String color) {
        CardInfoFragment fragment = new CardInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLOR, color);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_info, container, false);

        TextView colorInfoText = view.findViewById(R.id.color_info_text);

        if (getArguments() != null) {
            String color = getArguments().getString(ARG_COLOR, "Unknown");
            colorInfoText.setText("Цвет карточки: " + color);
        }

        view.setOnClickListener(v -> dismiss()); // Закрытие по нажатию
        return view;
    }
}
