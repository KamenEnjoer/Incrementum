package com.example.incrementum;

import static java.sql.Types.NULL;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class ToggleTurn {
    public boolean isBottomTurn = true; // Начальный ход — нижняя сторона
    public LinearLayout topCardsContainer;
    public LinearLayout bottomCardsContainer;

    public void toggleTurn(boolean isBottomTurn, Context context) {
        Activity activity = (Activity) context;
        topCardsContainer = activity.findViewById(R.id.top_cards_container);
        bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);

        // Обновляем состояние карточек
        toggleTurnUniversal(topCardsContainer, isBottomTurn);
        toggleTurnUniversal(bottomCardsContainer, !isBottomTurn);
    }

    public void toggleTurnUniversal(LinearLayout cardsContainer, boolean isTurn){
        for (int i = 0; i < cardsContainer.getChildCount(); i++) {
            View card = cardsContainer.getChildAt(i);
            if (isTurn) {
                card.setAlpha(0.5f);
                card.setEnabled(false);
            } else {
                card.setAlpha(1.0f);
                card.setEnabled(true);
            }
        }
    }
}
