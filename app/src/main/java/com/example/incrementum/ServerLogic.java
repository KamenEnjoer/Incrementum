package com.example.incrementum;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerLogic {
    public void fetchCardsByType(Context context, String requiredType, Consumer<List<Card>> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/cards")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("SERVER", "Server problem in CardsGeneration.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Type listType = new TypeToken<List<Card>>(){}.getType();
                    List<Card> cards = new Gson().fromJson(json, listType);

                    List<Card> filteredCards = cards.stream()
                            .filter(card -> card.getType().equalsIgnoreCase(requiredType))
                            .collect(Collectors.toList());
                    java.util.Collections.shuffle(filteredCards);
                    ((Activity) context).runOnUiThread(() -> callback.accept(filteredCards));
                }
            }
        });
    }

    public void fetchCardsById(Context context, String requiredId, Consumer<Card> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/cards")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("SERVER", "Server problem in CardsGeneration.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Type listType = new TypeToken<List<Card>>(){}.getType();
                    List<Card> cards = new Gson().fromJson(json, listType);

                    Card foundCard = null;
                    for (Card card : cards) {
                        if (card.getId().equalsIgnoreCase(requiredId)) {
                            foundCard = card;
                            break;
                        }
                    }
                    Card finalFoundCard = foundCard;
                    ((Activity) context).runOnUiThread(() -> callback.accept(finalFoundCard));
                }
            }
        });
    }
}
