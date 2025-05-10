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
    public static String currentPlayerName;


    private static Socket mSocket;
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
                Player playerOne = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(0);
                Player playerTwo = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(1);
                if (isNewGame){topPoints.setText(playerTwo.getName() + " turi " + playerTwo.getPoints() + " taškų.");
                    bottomPoints.setText(playerOne.getName() + " turi " + playerOne.getPoints() + " taškų.");
                } else {topPoints.setText(playerOne.getName() + " turi " + playerOne.getPoints() + " taškų.");
                    bottomPoints.setText(playerTwo.getName() + " turi " + playerTwo.getPoints() + " taškų.");
                }

                GridLayout gameGrid = findViewById(R.id.game_grid);
                topCardsContainer = findViewById(R.id.top_cards_container);
                bottomCardsContainer = findViewById(R.id.bottom_cards_container);

                GameFieldGeneration.generateGameField(gameGrid, this);

                cardsGeneration = new CardsGeneration();
                toggleTurn = new ToggleTurn();
                if (isNewGame) {
                    cardsGeneration.cardsGenerationOnStart(this);
                    GameStateRepository.getInstance().updateGameState(this, gameId,
                        GameStateRepository.getInstance().getCurrentGameState(),
                        ()->{
                            toggleTurn.initializeTurn(this, isNewGame);
                        },
                        (exception) -> Log.e("SERVER", "Error fetchGameStateById in MainMenu: " + exception.getMessage())
                    );
                }
                else {
                    cardsGeneration.cardsRefresh(this);
                    toggleTurn.initializeTurn(this, isNewGame);
                }

                playerOne = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(0);
                playerTwo = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(1);
                if (isNewGame) currentPlayerName = playerOne.getName();
                else currentPlayerName = playerTwo.getName();

                mSocket.connect();
                mSocket.emit("join_game", gameId);
                mSocket.on("gameStateUpdated", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(() -> {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                String json = data.toString();
                                GameState updatedGameState = new Gson().fromJson(json, GameState.class);
                                if (!updatedGameState.getCurrentTurn().equals(GameStateRepository.getInstance().getCurrentGameState().getCurrentTurn())){
                                    GameStateRepository.getInstance().setCurrentGameState(updatedGameState);

                                    cardsGeneration.cardsRefresh(MainActivity.this);

                                    GameFieldGeneration.generateGameField(gameGrid, MainActivity.this);
                                    toggleTurn.toggleTurn(MainActivity.this, topCardsContainer, false);

                                    boolean isMyTurn = updatedGameState.getCurrentTurn().equals(MainActivity.currentPlayerName);
                                    toggleTurn.toggleTurn(MainActivity.this, bottomCardsContainer, isMyTurn);
                                }
                            } catch (Exception e) {
                                Log.e("SOCKET.IO", "Error parsing received GameState", e);
                            }
                        });
                    }
                });
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
    }

    public static void emitGameState() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(GameStateRepository.getInstance().getCurrentGameState());
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e("SOCKET.IO", "Failed to convert JSON string to JSONObject", e);
            return;
        }

        mSocket.emit("update_game_state", jsonObject);
    }

    public void addNewCard(String type) {
        Player player;
        GameState currentGameState = GameStateRepository.getInstance().getCurrentGameState();
        if (currentGameState.getCurrentTurn().equals(currentGameState.getPlayers().get(0).getName())){
            player=GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(0);
        } else player=GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(1);
        cardsGeneration.oneCardGeneration(this, type, toggleTurn.currentPlayerContainer(this), player);
        toggleTurn.switchTurn(this);
    }
}