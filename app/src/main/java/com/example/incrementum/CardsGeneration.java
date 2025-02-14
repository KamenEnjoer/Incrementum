package com.example.incrementum;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardsGeneration {

    static public void generateCards(LinearLayout topCardsContainer, LinearLayout bottomCardsContainer, Context context) {
        generateDraggableCards(topCardsContainer, 3, "Top", context);
        generateDraggableCards(bottomCardsContainer, 3, "Bottom", context);
    }

    private static void generateDraggableCards(LinearLayout container, int count, String prefix, Context context) {
        for (int i = 0; i < count; i++) {
            TextView card = new TextView(context);
            card.setText(prefix + " " + (i + 1));
            card.setBackgroundResource(R.drawable.card_background);
            card.setGravity(android.view.Gravity.CENTER);
            card.setPadding(16, 16, 16, 16);

            card.setTag(prefix + "_" + i);

            int widthInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
            int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthInPx, heightInPx);
            params.setMargins(8, 8, 8, 8);
            card.setLayoutParams(params);

            card.setOnLongClickListener(v -> {
                ClipData.Item item = new ClipData.Item(v.getTag().toString());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            });;

            container.addView(card);
        }
    }
}
