package com.example.incrementum;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    ToggleTurn toggleTurn;
    CardsGeneration cardsGeneration;
    public Button plantsButton;
    public Button weatherButton;
    LinearLayout topCardsContainer;
    LinearLayout bottomCardsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plantsButton = findViewById(R.id.plants_button);
        plantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard("organizmas");
            }
        });
        weatherButton = findViewById(R.id.weather_button);
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard("oras");
            }
        });

        GridLayout gameGrid = findViewById(R.id.game_grid);
        topCardsContainer = findViewById(R.id.top_cards_container);
        bottomCardsContainer = findViewById(R.id.bottom_cards_container);

        GameFieldGeneration.generateGameField(gameGrid, this);

        cardsGeneration = new CardsGeneration();
        cardsGeneration.cardsGenerationOnStart(this, () -> {
            toggleTurn = new ToggleTurn();
            toggleTurn.initializeTurn(this);
        });
    }

    public void addNewCard(String type) {
        Consumer<Void> onCardGenerated = unused -> {
            toggleTurn.switchTurn(this);
        };
        cardsGeneration.oneCardGeneration(this, type, toggleTurn.currentPlayer(), onCardGenerated);
    }
}