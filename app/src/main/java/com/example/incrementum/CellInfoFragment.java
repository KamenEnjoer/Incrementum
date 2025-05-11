package com.example.incrementum;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        ImageView imageView = view.findViewById(R.id.info_image);

        int row = (cellTag.charAt(0) - 'A') + 1;
        int column = Character.getNumericValue(cellTag.charAt(1));
        Cell capturedCell = GameStateRepository.getInstance().getCurrentGameState().getBoard().get("row" + row).get("column" + column);
        Card plantCard = null;
        Card weatherCard = null;

        if (!capturedCell.getPlantCardId().isEmpty()){
            String id = capturedCell.getPlantCardId();
            plantCard = CardsRepository.getInstance().getCardById(id);
            int imageResId = requireContext().getResources().getIdentifier(plantCard.getImageName(), "drawable", requireContext().getPackageName());
            if (imageResId != 0) imageView.setImageResource(imageResId);
        }
        else imageView.setImageResource(R.drawable.card_background);
        if (!capturedCell.getWeatherCardId().isEmpty()){
            String id = capturedCell.getWeatherCardId();
            weatherCard = CardsRepository.getInstance().getCardById(id);
        }

        titleText.setText(cellTag);
        String info = "Čia ";
        if (plantCard!=null) {
            info += "auga " + plantCard.getName() + ".\n";
            if (capturedCell.getPlantLevel() == plantCard.getLevel()) info += "Jau išaugo.\n";
            else info += (plantCard.getDuration() - capturedCell.getPlantProgress()) + " žingsnis iki sekančio lygio.\n";
            info += plantCard.getName() + " turi " + capturedCell.getPlantHP() + " sveikatos taškų iš " + capturedCell.getPlantLevel() + ".\n";
            info += "Dabar šis langelis atneša " + capturedCell.getPlantLevel() + " taškų.\n";
        }
        else info += "niekas neauga.\n";
        if (weatherCard!=null) info+=weatherCard.getDescription();

        infoText.setText(info);

        view.setOnClickListener(v -> dismiss());
        return view;
    }
}
