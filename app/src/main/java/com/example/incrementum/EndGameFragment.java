package com.example.incrementum;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EndGameFragment extends DialogFragment {

    public static EndGameFragment newInstance() {
        EndGameFragment fragment = new EndGameFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_game, container, false);
        Button exit = view.findViewById(R.id.exit_button);
        TextView winnerText = view.findViewById(R.id.winner_text);

        Player winner=GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(0);
        if (winner.getPoints() < GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(1).getPoints()){
            winner = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(1);
        } else if (winner.getPoints() == GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(1).getPoints()){
            winner = null;
        }
        if (winner==null) winnerText.setText("Lygiosios!");
        else winnerText.setText("Laimėjo: \n" + winner.getName());;

        exit.setOnClickListener(v -> {
            if (getActivity() != null) {
                dismiss();
                MainActivity.leaveGameSession();
                Intent intent = new Intent(getActivity(), MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}