package com.example.incrementum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMenu extends AppCompatActivity {
    private ListView gameListView;
    private Button newGameButton;
    private ArrayAdapter<String> adapter;
    private List<String> gameIds = new ArrayList<>();
    public GameState defaultGameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        gameListView = findViewById(R.id.game_list_view);
        newGameButton = findViewById(R.id.new_game);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gameIds);
        gameListView.setAdapter(adapter);

        GameStateRepository.getInstance().fetchGameState(this, () -> {
                    List<GameState> allGames = GameStateRepository.getInstance().getGameStates();
                    gameIds.clear();
                    for (GameState state : allGames) {
                        gameIds.add(state.getId());
                    }
                    adapter.notifyDataSetChanged();
                },
                (exception) -> Log.e("SERVER", "Error fetchingGameState in MainMenu: " + exception.getMessage())
        );

        gameListView.setOnItemClickListener((parent, view, position, id) -> {
            GameStateRepository.getInstance().fetchGameStateById(this, gameIds.get(position),
                gameState -> {
                    PlayersRepository.getInstance().initializePlayers(gameState.getPlayers().get(1), gameState.getPlayers().get(0));
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
            defaultGameState.setCurrentTurn(PlayersRepository.getInstance().getPlayerOne().getName());

            GameStateRepository.getInstance().createGameState(this, defaultGameState, () -> {
            }, (exception) -> Log.e("SERVER", "Error fetchGameStateById in MainMenu: " + exception.getMessage()),
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

    private Map<String, Map<String, Cell>> generateEmptyBoard() {
        Map<String, Map<String, Cell>> board = new HashMap<>();
        for (int row = 1; row <= 6; row++) {
            Map<String, Cell> rowMap = new HashMap<>();
            for (int col = 1; col <= 6; col++) {
                Cell cell = new Cell("", "",0,0, 0, "",0);
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