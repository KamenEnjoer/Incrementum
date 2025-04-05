package com.example.incrementum;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CardsGeneration {
    public void fetchCardsFromServer(Context context, Consumer<List<Card>> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/cards")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Type listType = new TypeToken<List<Card>>(){}.getType();
                    List<Card> cards = new Gson().fromJson(json, listType);

                    ((Activity) context).runOnUiThread(() -> callback.accept(cards));
                }
            }
        });
    }

    public void generateCards(Context context) {
        Activity activity = (Activity) context;
        LinearLayout topCardsContainer = activity.findViewById(R.id.top_cards_container);
        LinearLayout bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);

        fetchCardsFromServer(context, cards -> {
            for (Card card : cards) {
                generateDraggableCards(topCardsContainer, card, context);
                generateDraggableCards(bottomCardsContainer, card, context);
            }
        });
    }

    public void generateDraggableCards(LinearLayout container, Card card, Context context) {
        String prefix = container.getId() == R.id.top_cards_container ? "Top" : "Bottom";

        // Создаём контейнер для карточки
        LinearLayout cardContainer = new LinearLayout(context);
        cardContainer.setOrientation(LinearLayout.VERTICAL);
        cardContainer.setGravity(android.view.Gravity.CENTER);
        cardContainer.setPadding(16, 16, 16, 16);
        cardContainer.setTag(prefix + "_" + card.getType());

        // Устанавливаем цвет фона контейнера
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

        cardContainer.setOnClickListener(v -> {
            String color = card.getType().equals("organizmas") ? "Organizmas" : "Oras";
            InfoFragment infoFragment = InfoFragment.newInstance("Цвет карточки: " + color);
            infoFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "cardInfo");
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
