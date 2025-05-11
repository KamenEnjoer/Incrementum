package com.example.incrementum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Handler;

public class MainMenu extends AppCompatActivity {
    private ListView gameListView;
    private Button newGameButton;
    private ArrayAdapter<String> adapter;
    private List<String> gameIds = new ArrayList<>();
    public GameState defaultGameState;

    private Handler handler = new Handler();
    private final int REFRESH_INTERVAL = 1000;

    private final Runnable refreshGameListRunnable = new Runnable() {
        @Override
        public void run() {
            fetchAndDisplayAvailableGames();
            handler.postDelayed(this, REFRESH_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        gameListView = findViewById(R.id.game_list_view);
        newGameButton = findViewById(R.id.new_game);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gameIds);
        gameListView.setAdapter(adapter);

        fetchAndDisplayAvailableGames();
        handler.postDelayed(refreshGameListRunnable, REFRESH_INTERVAL);

        gameListView.setOnItemClickListener((parent, view, position, id) -> {
            GameStateRepository.getInstance().fetchGameStateById(this, gameIds.get(position),
                    gameState -> {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("GameStateId", gameIds.get(position));
                        startActivity(intent);
                    }, (exception) -> Log.e("SERVER", "Error fetchGameStateById in MainMenu: " + exception.getMessage())
            );
        });

        newGameButton.setOnClickListener(v -> {
            defaultGameState = new GameState();
            defaultGameState.setPlayers(generateDefaultPlayers());
            defaultGameState.setBoard(generateEmptyBoard());
            defaultGameState.setCurrentTurn(defaultGameState.getPlayers().get(0).getName());

            GameStateRepository.getInstance().createGameState(this, defaultGameState, () -> {
                    }, (exception) -> Log.e("SERVER", "Error createGameState in MainMenu: " + exception.getMessage()),
                    id -> {
                        GameStateRepository.getInstance().fetchGameStateById(this, id,
                                gameState -> {
                                    Intent intent = new Intent(this, MainActivity.class);
                                    intent.putExtra("GameStateId", id);
                                    intent.putExtra("IsNewGame", true);
                                    startActivity(intent);
                                }, (exception) -> Log.e("SERVER", "Error fetchGameStateById in MainMenu: " + exception.getMessage())
                        );
                    });
        });
    }

    private void fetchAndDisplayAvailableGames() {
        GameStateRepository.getInstance().fetchGameState(this, () -> {
            List<GameState> allGames = GameStateRepository.getInstance().getGameStates();
            gameIds.clear();
            for (GameState state : allGames) {
                if (state.getConnectedPlayers() < 2) {
                    gameIds.add(state.getId());
                }
            }
            adapter.notifyDataSetChanged();
        }, (exception) -> Log.e("SERVER", "Error fetchingGameState in MainMenu: " + exception.getMessage()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshGameListRunnable);
    }

    private Map<String, Map<String, Cell>> generateEmptyBoard() {
        Map<String, Map<String, Cell>> board = new HashMap<>();
        for (int row = 1; row <= 6; row++) {
            Map<String, Cell> rowMap = new HashMap<>();
            for (int col = 1; col <= 6; col++) {
                Cell cell = new Cell("", "", 0, 0, 0, "", 0);
                rowMap.put("column" + col, cell);
            }
            board.put("row" + row, rowMap);
        }
        return board;
    }

    private List<Player> generateDefaultPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player 1"));
        players.add(new Player("Player 2"));
        return players;
    }
}
