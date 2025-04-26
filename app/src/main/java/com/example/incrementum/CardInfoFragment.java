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

public class CardInfoFragment extends DialogFragment {

    private static final String ARG_CARD = "card";

    public static CardInfoFragment newInstance(Card card) {
        CardInfoFragment fragment = new CardInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD, card);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        Card card = (Card) getArguments().getSerializable(ARG_CARD);

        TextView infoText = view.findViewById(R.id.description_text);
        TextView titleText = view.findViewById(R.id.title_text);

        if (getArguments() != null) {
            titleText.setText(card.getName());
            String description="";
            if (Objects.equals(card.getType(), "oras")) description = card.getDescription() +
                    "\n\nTipas: " + card.getType() +
                    "\nStiprumas: " + card.getLevel() +
                    "\nTrukmė: " + card.getDuration() + " ž." +
                    "\nPlotis: " + card.getSquare();
            else description = card.getDescription() +
                    "\n\nTipas: " + card.getType() +
                    "\nMaksimalus dydis: " + card.getLevel() +
                    "\nAuga: " + card.getDuration() + " ž.";
            infoText.setText(description);
        }

        view.setOnClickListener(v -> dismiss());
        return view;
    }
}
