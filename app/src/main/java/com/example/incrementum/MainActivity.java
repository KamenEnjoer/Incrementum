package com.example.incrementum;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ToggleTurn toggleTurn;
    CardsGeneration cardsGeneration;
    public Button plantsButton;
    public Button weatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gameGrid = findViewById(R.id.game_grid);
        GameFieldGeneration.generateGameField(gameGrid, this);

        cardsGeneration =  new CardsGeneration();
        cardsGeneration.generateCards(this);
        toggleTurn = new ToggleTurn();
        toggleTurn.initializeTurn(this);

        plantsButton = findViewById(R.id.plants_button);
        plantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard("green");
            }
        });
        weatherButton = findViewById(R.id.weather_button);
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard("blue");
            }
        });
    }

    public void addNewCard(String type){
        cardsGeneration.generateDraggableCards(toggleTurn.currentPlayer(), type, this);
        toggleTurn.switchTurn(this);
    }
}

