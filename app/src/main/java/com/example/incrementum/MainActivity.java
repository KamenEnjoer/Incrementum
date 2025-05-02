package com.example.incrementum;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ToggleTurn toggleTurn;
    CardsGeneration cardsGeneration;
    public Button plantsButton;
    public Button weatherButton;
    LinearLayout topCardsContainer;
    LinearLayout bottomCardsContainer;
    TextView bottomPoints;
    TextView topPoints;
    public static String gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameId = getIntent().getStringExtra("GameStateId");

        CardsRepository.getInstance().fetchCards(this,
                () -> {
                    bottomPoints = findViewById(R.id.bottom_points);
                    topPoints =  findViewById(R.id.top_points);
                    bottomPoints.setText(PlayersRepository.getInstance().getPlayerOne().getName() + " turi 0 taškų.");
                    topPoints.setText(PlayersRepository.getInstance().getPlayerTwo().getName() + " turi 0 taškų.");

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
                    cardsGeneration.cardsGenerationOnStart(this);

                    toggleTurn = new ToggleTurn();
                    toggleTurn.initializeTurn(this);
                },
                (exception) -> {Log.e("SERVER", "Error of cards loading: " + exception.getMessage());}
        );
    }

    public void addNewCard(String type) {
        cardsGeneration.oneCardGeneration(this, type, toggleTurn.currentPlayerContainer(), PlayersRepository.getInstance().getCurrentPlayer());
        toggleTurn.switchTurn(this);
    }
}