package com.example.incrementum;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class GameStateRepository {
    private static GameStateRepository instance;
    private GameState currentGameState;
    private List<GameState> gameStates;

    private GameStateRepository() { }

    public static synchronized GameStateRepository getInstance() {
        if (instance == null) {
            instance = new GameStateRepository();
        }
        return instance;
    }

    public void fetchGameState(Context context, Runnable onSuccess, Consumer<Exception> onError) {
        if (gameStates != null && !gameStates.isEmpty()) {
            onSuccess.run();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/gamestates")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SERVER", "Server problem in GameStateRepository.");
                ((android.app.Activity) context).runOnUiThread(() -> onError.accept(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Type listType = new TypeToken<List<GameState>>(){}.getType();
                    gameStates = new Gson().fromJson(json, listType);

                    ((android.app.Activity) context).runOnUiThread(onSuccess);
                } else {
                    ((android.app.Activity) context).runOnUiThread(() ->
                            onError.accept(new IOException("Server returned error code: " + response.code()))
                    );
                }
            }
        });
    }

    public void fetchGameStateById(Context context, String gameStateId,
                                   Consumer<GameState> onSuccess,
                                   Consumer<Exception> onError) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/gamestates/" + gameStateId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SERVER", "Server problem in GameStateRepository.");
                ((Activity) context).runOnUiThread(() -> onError.accept(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    currentGameState = new Gson().fromJson(json, GameState.class);

                    ((Activity) context).runOnUiThread(() -> onSuccess.accept(currentGameState));
                } else {
                    ((Activity) context).runOnUiThread(() ->
                            onError.accept(new IOException("Server returned error code: " + response.code()))
                    );
                }
            }
        });
    }

    public void createGameState(Context context, GameState newGameState,
                                Runnable onSuccess,
                                Consumer<Exception> onError,
                                Consumer<String> onIdReceived) {

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String json = gson.toJson(newGameState);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/gamestates")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity) context).runOnUiThread(() -> onError.accept(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    GameState createdGameState = gson.fromJson(responseBody, GameState.class);

                    ((Activity) context).runOnUiThread(() -> {
                        onIdReceived.accept(createdGameState.getId());
                        onSuccess.run();
                    });
                } else {
                    ((Activity) context).runOnUiThread(() ->
                            onError.accept(new IOException("Server returned error code: " + response.code())));
                }
            }
        });
    }

    public void updateGameState(Context context, String gameStateId,
                                GameState updatedGameState,
                                Runnable onSuccess,
                                Consumer<Exception> onError) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String json = gson.toJson(updatedGameState);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/gamestates/" + gameStateId)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SERVER", "Failed to update GameState in GameStateRepository: " + e.getMessage());
                ((Activity) context).runOnUiThread(() -> onError.accept(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ((Activity) context).runOnUiThread(onSuccess);
                } else {
                    ((Activity) context).runOnUiThread(() ->
                            onError.accept(new IOException("Server returned error code: " + response.code()))
                    );
                }
            }
        });
    }

    public GameState getCurrentGameState() {return currentGameState;}

    public List<GameState> getGameStates() {return gameStates;}

    public void setCurrentGameState(GameState currentGameState) {this.currentGameState = currentGameState;}
}
