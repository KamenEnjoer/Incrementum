package com.example.incrementum;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CardsRepository {
    private static CardsRepository instance;
    private List<Card> cards = new ArrayList<>();

    private CardsRepository() { }

    public static synchronized CardsRepository getInstance() {
        if (instance == null) {
            instance = new CardsRepository();
        }
        return instance;
    }

    public void fetchCards(Context context, Runnable onSuccess, Consumer<Exception> onError) {
        if (!cards.isEmpty()) {
            onSuccess.run();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/cards")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SERVER", "Server problem in CardsRepository.");
                ((android.app.Activity) context).runOnUiThread(() -> onError.accept(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Type listType = new TypeToken<List<Card>>(){}.getType();
                    cards = new Gson().fromJson(json, listType);

                    ((android.app.Activity) context).runOnUiThread(onSuccess);
                } else {
                    ((android.app.Activity) context).runOnUiThread(() ->
                            onError.accept(new IOException("Server returned error code: " + response.code()))
                    );
                }
            }
        });
    }

    public Card getCardById(String id) {
        if (cards == null) return null;
        for (Card card : cards) {
            if (card.getId().equalsIgnoreCase(id)) {
                return card;
            }
        }
        return null;
    }

    public List<Card> getCardByType(String type) {
        if (cards == null) return null;
        List<Card> cardsByType = new ArrayList<>();
        for (Card card : cards) {
            if (card.getType().equalsIgnoreCase(type)) {
                cardsByType.add(card);
            }
        }
        java.util.Collections.shuffle(cardsByType);
        return cardsByType;
    }
}
