package com.example.incrementum;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

public class GameStateRepository {
    private static GameStateRepository instance;
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

    public GameState getFirstGameState() {
        return (gameStates != null && !gameStates.isEmpty()) ? gameStates.get(0) : null;
    }
}
