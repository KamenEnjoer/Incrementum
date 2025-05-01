package com.example.incrementum;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ToggleTurn {
    private static boolean isBottomTurn = true;
    private static LinearLayout topCardsContainer;
    private static LinearLayout bottomCardsContainer;
    private static ProgressBar stepsCounter;

    public void initializeTurn(Context context) {
        Activity activity = (Activity) context;
        topCardsContainer = activity.findViewById(R.id.top_cards_container);
        bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);

        toggleTurn(topCardsContainer, false);
        toggleTurn(bottomCardsContainer, true);
    }

    public void switchTurn(Context context) {
        Activity activity = (Activity) context;
        stepsCounter = activity.findViewById(R.id.steps_counter);
        stepsCounter.setProgress(stepsCounter.getProgress()+1);
        if (stepsCounter.getProgress() == 100){
            return;
        }

        isBottomTurn = !isBottomTurn;

        toggleTurn(topCardsContainer, !isBottomTurn);
        toggleTurn(bottomCardsContainer, isBottomTurn);
        PlayersRepository.getInstance().switchTurn();
        GameStateRepository.getInstance().updateGameState(context, MainActivity.gameId, MainActivity.defaultGameState,
                ()->{},
                (exception) -> {Log.e("SERVER", "Error in ToggleTurn: " + exception.getMessage());}
        );
    }

    private void toggleTurn(LinearLayout cardsContainer, boolean isTurn) {
        for (int i = 0; i < cardsContainer.getChildCount(); i++) {
            View card = cardsContainer.getChildAt(i);
            card.setAlpha(isTurn ? 1.0f : 0.5f);
            card.setEnabled(isTurn);
        }
    }

    public LinearLayout currentPlayerContainer(){
        if (isBottomTurn) return bottomCardsContainer;
        else return topCardsContainer;
    }
}
