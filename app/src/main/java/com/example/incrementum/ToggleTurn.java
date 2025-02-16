package com.example.incrementum;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class ToggleTurn {
    private static boolean isBottomTurn = true;
    private static LinearLayout topCardsContainer;
    private static LinearLayout bottomCardsContainer;

    public void initializeTurn(Context context) {
        Activity activity = (Activity) context;
        topCardsContainer = activity.findViewById(R.id.top_cards_container);
        bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);

        toggleTurn(topCardsContainer, false);
        toggleTurn(bottomCardsContainer, true);
    }

    public void switchTurn(Context context) {
        isBottomTurn = !isBottomTurn;

        toggleTurn(topCardsContainer, !isBottomTurn);
        toggleTurn(bottomCardsContainer, isBottomTurn);
    }

    private void toggleTurn(LinearLayout cardsContainer, boolean isTurn) {
        for (int i = 0; i < cardsContainer.getChildCount(); i++) {
            View card = cardsContainer.getChildAt(i);
            card.setAlpha(isTurn ? 1.0f : 0.5f);
            card.setEnabled(isTurn);
        }
    }

    public LinearLayout currentPlayer(){
        if (isBottomTurn) return bottomCardsContainer;
        else return topCardsContainer;
    }
}
