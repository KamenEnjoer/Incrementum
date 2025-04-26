package com.example.incrementum;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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

public class CardsGeneration {
    public void fetchCardsFromServer(Context context, String requiredType, Consumer<List<Card>> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/cards")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("SERVER", "Server problem.");
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

    public void cardsGenerationOnStart(Context context, Runnable onCardsGenerated) {
        Activity activity = (Activity) context;
        LinearLayout topCardsContainer = activity.findViewById(R.id.top_cards_container);
        LinearLayout bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);

        final int totalCardsNeeded = 8; // 3*2 + 1*2 = 8 карт
        final int[] cardsGenerated = {0}; // Массив для обхода final-проверки в анонимных классах

        Consumer<Void> onCardGenerated = unused -> {
            cardsGenerated[0]++;
            if (cardsGenerated[0] == totalCardsNeeded) onCardsGenerated.run();
        };

        for (int i = 0; i < 3; i++) {
            oneCardGeneration(context, "organizmas", topCardsContainer, onCardGenerated);
            oneCardGeneration(context, "organizmas", bottomCardsContainer, onCardGenerated);
        }
        oneCardGeneration(context, "oras", topCardsContainer, onCardGenerated);
        oneCardGeneration(context, "oras", bottomCardsContainer, onCardGenerated);
    }


    public void oneCardGeneration(Context context, String cardType, LinearLayout cardsContainer, Consumer<Void> onComplete) {
        fetchCardsFromServer(context, cardType, cards -> {
            generateDraggableCards(cardsContainer, cards.get(0), context);
            onComplete.accept(null);
        });
    }

    public void generateDraggableCards(LinearLayout container, Card card, Context context) {
        String prefix = container.getId() == R.id.top_cards_container ? "Top" : "Bottom";

        LinearLayout cardContainer = new LinearLayout(context);
        cardContainer.setOrientation(LinearLayout.VERTICAL);
        cardContainer.setGravity(android.view.Gravity.CENTER);
        cardContainer.setPadding(16, 16, 16, 16);
        cardContainer.setTag(prefix + "_" + card.getType());

        int backgroundColor = card.getType().equals("organizmas") ? (prefix.equals("Top") ? 0xFF99FF99 : 0xFF228B22)
                : (prefix.equals("Top") ? 0xFF99CCFF : 0xFF0000CD);
        cardContainer.setBackgroundColor(backgroundColor);

        // Создаём картинку (например, заглушка)
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // Тут можно вставить реальное изображение из ресурсов или URL

        // Создаём текст (имя карточки)
        TextView textView = new TextView(context);
        textView.setText(card.getName());  // Используем имя карточки из объекта Card
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setTextSize(16);
        textView.setPadding(0, 8, 0, 0);

        cardContainer.addView(imageView);
        cardContainer.addView(textView);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 350);
        params.setMargins(8, 8, 8, 8);
        cardContainer.setLayoutParams(params);

        //ФРАГМЕНТ ИНФОРМАЦИИ
        cardContainer.setOnClickListener(v -> {
            CardInfoFragment cardInfoFragment = CardInfoFragment.newInstance(card);
            cardInfoFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "cardInfo");
        });

        cardContainer.setOnLongClickListener(v -> {
            ClipData.Item item = new ClipData.Item(v.getTag().toString());
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });
        container.addView(cardContainer);
    }
}
