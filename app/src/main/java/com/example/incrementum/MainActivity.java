package com.example.incrementum;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public LinearLayout topCardsContainer;
    public LinearLayout bottomCardsContainer;
    public ToggleTurn toggleTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gameGrid = findViewById(R.id.game_grid);
        GameFieldGeneration.generateGameField(gameGrid, this);

        topCardsContainer = findViewById(R.id.top_cards_container);
        bottomCardsContainer = findViewById(R.id.bottom_cards_container);
        toggleTurn = new ToggleTurn();
        toggleTurn.toggleTurn(true, topCardsContainer, bottomCardsContainer, this);
        CardsGeneration.generateCards(topCardsContainer, bottomCardsContainer, this);
    }

    public void endTurn(boolean turn) {
        toggleTurn.toggleTurn(turn, topCardsContainer, bottomCardsContainer, this);
    }
}

