package com.example.incrementum;

import android.os.Bundle;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public ToggleTurn toggleTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gameGrid = findViewById(R.id.game_grid);
        GameFieldGeneration.generateGameField(gameGrid, this);

        CardsGeneration.generateCards(this);
        ToggleTurn toggleTurn = new ToggleTurn();
        toggleTurn.initializeTurn(this);
    }
}

