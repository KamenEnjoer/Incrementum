package com.example.incrementum;

import static java.sql.Types.NULL;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class ToggleTurn {
    public boolean isBottomTurn = true; // Начальный ход — нижняя сторона

    // Метод для переключения хода
    public void toggleTurn(boolean turn, LinearLayout topCardsContainer, LinearLayout bottomCardsContainer, Context context) {
        isBottomTurn = turn;
        // Обновляем состояние верхних карточек
        for (int i = 0; i < topCardsContainer.getChildCount(); i++) {
            View card = topCardsContainer.getChildAt(i);
            if (isBottomTurn) {
                card.setAlpha(0.5f); // Делаем карточку полупрозрачной
                card.setEnabled(false); // Блокируем карточку
            } else {
                card.setAlpha(1.0f); // Восстанавливаем видимость
                card.setEnabled(true); // Разблокируем карточку
            }
        }

        // Обновляем состояние нижних карточек
        for (int i = 0; i < bottomCardsContainer.getChildCount(); i++) {
            View card = bottomCardsContainer.getChildAt(i);
            if (isBottomTurn) {
                card.setAlpha(1.0f); // Восстанавливаем видимость
                card.setEnabled(true); // Разблокируем карточку
            } else {
                card.setAlpha(0.5f); // Делаем карточку полупрозрачной
                card.setEnabled(false); // Блокируем карточку
            }
        }
    }
}
