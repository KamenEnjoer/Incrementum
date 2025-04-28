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

import java.util.function.Consumer;

public class CardsGeneration {
    public void cardsGenerationOnStart(Context context) {
        Activity activity = (Activity) context;
        LinearLayout topCardsContainer = activity.findViewById(R.id.top_cards_container);
        LinearLayout bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);

        for (int i = 0; i < 3; i++) {
            oneCardGeneration(context, "organizmas", topCardsContainer);
            oneCardGeneration(context, "organizmas", bottomCardsContainer);
        }
        oneCardGeneration(context, "oras", topCardsContainer);
        oneCardGeneration(context, "oras", bottomCardsContainer);
    }


    public void oneCardGeneration(Context context, String cardType, LinearLayout cardsContainer) {
        Card card = CardsRepository.getInstance().getCardByType(cardType).get(0);
        generateDraggableCards(cardsContainer, card, context);
    }

    public void generateDraggableCards(LinearLayout container, Card card, Context context) {
        String prefix = container.getId() == R.id.top_cards_container ? "Top" : "Bottom";

        LinearLayout cardContainer = new LinearLayout(context);
        cardContainer.setOrientation(LinearLayout.VERTICAL);
        cardContainer.setGravity(android.view.Gravity.CENTER);
        cardContainer.setPadding(16, 16, 16, 16);
        cardContainer.setTag(prefix + "_" + card.getType());
        cardContainer.setTag(cardContainer.getTag() + "*" + card.getId());

        int backgroundColor = card.getType().equals("organizmas") ? (prefix.equals("Top") ? 0xFF99FF99 : 0xFF228B22)
                : (prefix.equals("Top") ? 0xFF99CCFF : 0xFF0000CD);
        cardContainer.setBackgroundColor(backgroundColor);

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int imageResId = context.getResources().getIdentifier(card.getImageName(), "drawable", context.getPackageName());
        if (imageResId != 0) imageView.setImageResource(imageResId);

        TextView textView = new TextView(context);
        textView.setText(card.getName());
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
