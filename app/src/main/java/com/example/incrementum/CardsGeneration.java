package com.example.incrementum;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardsGeneration {
    static public void generateCards(Context context) {
        Activity activity = (Activity) context;
        LinearLayout topCardsContainer = activity.findViewById(R.id.top_cards_container);
        LinearLayout bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);
        for (int i=0; i<3; i++){
            generateDraggableCards(topCardsContainer, "Top", "green", context);
            generateDraggableCards(bottomCardsContainer,"Bottom", "green", context);
        }
        generateDraggableCards(topCardsContainer, "Top", "blue", context);
        generateDraggableCards(bottomCardsContainer,"Bottom", "blue", context);
    }

    private static void generateDraggableCards(LinearLayout container, String prefix, String type, Context context) {
        TextView card = new TextView(context);
        card.setText(prefix);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(16, 16, 16, 16);
        card.setTag(prefix + "_" + type);

        if (prefix.equals("Top")) {
            card.setBackgroundColor(type.equals("green") ? 0xFF99FF99 : 0xFF99CCFF); // Светло-зелёный или светло-голубой
        } else {
            card.setBackgroundColor(type.equals("green") ? 0xFF228B22 : 0xFF0000CD); // Тёмно-зелёный или тёмно-голубой
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 100);
        params.setMargins(8, 8, 8, 8);
        card.setLayoutParams(params);

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
