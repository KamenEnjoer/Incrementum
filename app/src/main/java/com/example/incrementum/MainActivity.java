package com.example.incrementum;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ToggleTurn toggleTurn;
    CardsGeneration cardsGeneration;
    public Button plantsButton;
    public Button weatherButton;
    LinearLayout topCardsContainer;
    LinearLayout bottomCardsContainer;
    public static String gameId;
    public static GameState defaultGameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardsRepository.getInstance().fetchCards(this,
                () -> {
                    defaultGameState = new GameState();
                    defaultGameState.setBoard(generateEmptyBoard());
                    defaultGameState.setPlayers(generateDefaultPlayers());
                    defaultGameState.setCurrentTurn(PlayersRepository.getInstance().getCurrentPlayer().getName());

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

                    GameStateRepository.getInstance().createGameState(this, defaultGameState,
                            () -> {
                                Log.d("SERVER", "GameState created successfully");
                            },
                            (exception) -> Log.e("SERVER", "Error creating GameState: " + exception.getMessage()),
                            id -> {
                                gameId = id;
                            }
                    );
                },
                (exception) -> {Log.e("SERVER", "Error of cards loading: " + exception.getMessage());}
        );
    }

    public void addNewCard(String type) {
        cardsGeneration.oneCardGeneration(this, type, toggleTurn.currentPlayerContainer(), PlayersRepository.getInstance().getCurrentPlayer());
        toggleTurn.switchTurn(this);
    }

    private Map<String, Map<String, Cell>> generateEmptyBoard() {
        Map<String, Map<String, Cell>> board = new HashMap<>();
        for (int row = 1; row <= 6; row++) {
            Map<String, Cell> rowMap = new HashMap<>();
            for (int col = 1; col <= 6; col++) {
                Cell cell = new Cell("",0,0,"",0);
                rowMap.put("column" + col, cell);
            }
            board.put("row" + row, rowMap);
        }
        return board;
    }
    private List<Player> generateDefaultPlayers() {
        List<Player> players = new ArrayList<>();
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        PlayersRepository.getInstance().initializePlayers(player1, player2);

        players.add(player1);
        players.add(player2);
        return players;
    }
}