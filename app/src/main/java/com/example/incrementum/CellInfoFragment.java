package com.example.incrementum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class CellInfoFragment extends DialogFragment {

    private static final String ARG_CELL = "cellTag";

    public static CellInfoFragment newInstance(String cellTag) {
        CellInfoFragment fragment = new CellInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CELL, cellTag);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        String cellTag = getArguments().getSerializable(ARG_CELL).toString();

        TextView infoText = view.findViewById(R.id.description_text);
        TextView titleText = view.findViewById(R.id.title_text);

        titleText.setText(cellTag.substring(0, 2));
        infoText.setText("Состояние клетки:\n" + cellTag);

        view.setOnClickListener(v -> dismiss());
        return view;
    }
}
