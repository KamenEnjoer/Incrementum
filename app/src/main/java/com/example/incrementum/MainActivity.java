package com.example.incrementum;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import org.json.JSONException;

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

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:3000");
        } catch (Exception e) {
            Log.e("SOCKET.IO", "Error initializing socket", e);
        }
    }

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
                                    toggleTurn = new ToggleTurn();
                                    toggleTurn.initializeTurn(this);
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

        mSocket.connect();
        mSocket.emit("join_game", gameId);
        mSocket.on("gameStateUpdated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(() -> {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.d("SOCKET.IO", "Received updated GameState");
                        String json = data.toString();

                        GameState updatedGameState = new Gson().fromJson(json, GameState.class);
                        GameStateRepository.getInstance().updateGameState(MainActivity.this, gameId, updatedGameState,
                                ()->{},
                                (exception) -> {Log.e("SERVER", "Error updating GameState", exception);}
                        );
                        cardsGeneration.cardsRefresh(MainActivity.this);
                        toggleTurn.initializeTurn(MainActivity.this);

                        Player playerOne = PlayersRepository.getInstance().getPlayerOne();
                        Player playerTwo = PlayersRepository.getInstance().getPlayerTwo();
                        topPoints.setText(playerOne.getName() + " turi " + playerOne.getPoints() + " taškų.");
                        bottomPoints.setText(playerTwo.getName() + " turi " + playerTwo.getPoints() + " taškų.");

                    } catch (Exception e) {
                        Log.e("SOCKET.IO", "Error parsing received GameState", e);
                    }
                });
            }
        });
    }

    public void emitGameState() {
        Gson gson = new Gson();
        mSocket.emit("update_game_state", gson.toJson(GameStateRepository.getInstance().getCurrentGameState()));
    }

    public void addNewCard(String type) {
        cardsGeneration.oneCardGeneration(this, type, toggleTurn.currentPlayerContainer(this), PlayersRepository.getInstance().getCurrentPlayer());
        toggleTurn.switchTurn(this);

        emitGameState();
    }
}