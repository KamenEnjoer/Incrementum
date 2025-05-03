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
    public boolean isNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameId = getIntent().getStringExtra("GameStateId");
        isNewGame = getIntent().getBooleanExtra("IsNewGame", false);

        CardsRepository.getInstance().fetchCards(this,
                () -> {
                    bottomPoints = findViewById(R.id.bottom_points);
                    topPoints =  findViewById(R.id.top_points);
                    Player playerOne = PlayersRepository.getInstance().getPlayerOne();
                    Player playerTwo = PlayersRepository.getInstance().getPlayerTwo();
                    topPoints.setText(playerOne.getName() + " turi " + playerOne.getPoints() + " taškų.");
                    bottomPoints.setText(playerTwo.getName() + " turi " + playerTwo.getPoints() + " taškų.");

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
                    if (isNewGame) {
                        cardsGeneration.cardsGenerationOnStart(this);
                        GameStateRepository.getInstance().fetchGameStateById(this,
                            gameId,
                            gameState -> {
                                GameStateRepository.getInstance().getCurrentGameState().setPlayers(PlayersRepository.getInstance().getPlayers());
                                GameStateRepository.getInstance().updateGameState(this,
                                    GameStateRepository.getInstance().getCurrentGameState().getId(),
                                    GameStateRepository.getInstance().getCurrentGameState(),
                                    () -> {

                                    },
                                    (exception) -> {Log.e("SERVER", "Error of updating in MainActivity: " + exception.getMessage());}
                                );
                            },
                            (exception) -> {Log.e("SERVER", "Error of fetching in MainActivity: " + exception.getMessage());}
                        );
                    }
                    else {
                        GameStateRepository.getInstance().fetchGameStateById(this,
                            GameStateRepository.getInstance().getCurrentGameState().getId(),
                            gameState -> {
                                PlayersRepository.getInstance().setPlayers(gameState.getPlayers().get(0), gameState.getPlayers().get(1));
                                cardsGeneration.cardsRefresh(this);
                                toggleTurn = new ToggleTurn();
                                toggleTurn.initializeTurn(this);
                            },
                            (exception) -> {Log.e("SERVER", "Error of fetching in MainActivity: " + exception.getMessage());}
                        );
                    }
                },
                (exception) -> {Log.e("SERVER", "Error of cards loading: " + exception.getMessage());}
        );
    }

    public void addNewCard(String type) {
        cardsGeneration.oneCardGeneration(this, type, toggleTurn.currentPlayerContainer(), PlayersRepository.getInstance().getCurrentPlayer());
        toggleTurn.switchTurn(this);
    }
}