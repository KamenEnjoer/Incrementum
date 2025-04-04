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


public class CardsGeneration {
    public void generateCards(Context context) {
        Activity activity = (Activity) context;
        LinearLayout topCardsContainer = activity.findViewById(R.id.top_cards_container);
        LinearLayout bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);
        for (int i=0; i<3; i++){
            generateDraggableCards(topCardsContainer,"green", context);
            generateDraggableCards(bottomCardsContainer, "green", context);
        }
        generateDraggableCards(topCardsContainer, "blue", context);
        generateDraggableCards(bottomCardsContainer, "blue", context);
    }

    public void generateDraggableCards(LinearLayout container, String type, Context context) {
        String prefix = container.getId() == R.id.top_cards_container ? "Top" : "Bottom";

        // Создаём контейнер для карточки
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(16, 16, 16, 16);
        card.setTag(prefix + "_" + type);

        // Устанавливаем цвет фона контейнера
        int backgroundColor = type.equals("green") ? (prefix.equals("Top") ? 0xFF99FF99 : 0xFF228B22)
                : (prefix.equals("Top") ? 0xFF99CCFF : 0xFF0000CD);
        card.setBackgroundColor(backgroundColor);

        // Создаём картинку
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Создаём текст
        TextView textView = new TextView(context);
        textView.setText(prefix);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setTextSize(16);
        textView.setPadding(0, 8, 0, 0);

        card.addView(imageView);
        card.addView(textView);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 350);
        params.setMargins(8, 8, 8, 8);
        card.setLayoutParams(params);

        card.setOnClickListener(v -> {
            String color = type.equals("green") ? "Green" : "Blue";
            InfoFragment infoFragment = InfoFragment.newInstance("Цвет карточки: " + color);
            infoFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "cardInfo");
        });

        card.setOnLongClickListener(v -> {
            ClipData.Item item = new ClipData.Item(v.getTag().toString());
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });

        container.addView(card);
    }
}
