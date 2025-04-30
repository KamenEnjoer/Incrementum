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

import java.util.List;
import java.util.function.Consumer;

public class CardsGeneration {
    public void cardsGenerationOnStart(Context context) {
        Activity activity = (Activity) context;
        LinearLayout topCardsContainer = activity.findViewById(R.id.top_cards_container);
        LinearLayout bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);

        for (int i = 0; i < 3; i++) {
            oneCardGeneration(context, "organizmas", topCardsContainer, PlayersRepository.getInstance().getPlayerTwo());
            oneCardGeneration(context, "organizmas", bottomCardsContainer, PlayersRepository.getInstance().getPlayerOne());
        }
        oneCardGeneration(context, "oras", topCardsContainer, PlayersRepository.getInstance().getPlayerTwo());
        oneCardGeneration(context, "oras", bottomCardsContainer, PlayersRepository.getInstance().getPlayerOne());
    }

    public void oneCardGeneration(Context context, String cardType, LinearLayout cardsContainer, Player player) {
        Card card = CardsRepository.getInstance().getCardByType(cardType).get(0);
        generateDraggableCards(cardsContainer, card, context);
        player.addCardIdToHand(card.getId());
    }

    public void generateDraggableCards(LinearLayout container, Card card, Context context) {
        String prefix = container.getId() == R.id.top_cards_container ? "Top" : "Bottom";

        LinearLayout cardContainer = new LinearLayout(context);
        cardContainer.setOrientation(LinearLayout.VERTICAL);
        cardContainer.setGravity(android.view.Gravity.CENTER);
        cardContainer.setPadding(16, 16, 16, 16);
        cardContainer.setTag(prefix + "_" + card.getType());
        cardContainer.setTag(cardContainer.getTag() + "*" + card.getId());

        if (card.getType().equals("organizmas")) cardContainer.setBackgroundColor(0xff99cc00);
        else cardContainer.setBackgroundColor(0xff33b5e5);

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int imageResId = context.getResources().getIdentifier(card.getImageName(), "drawable", context.getPackageName());
        if (imageResId != 0) imageView.setImageResource(imageResId);

        TextView textView = new TextView(context);
        textView.setText(card.getName());
        textView.setTextColor(0xFFFFFFFF);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setTextSize(16);
        textView.setPadding(0, 8, 0, 0);

        cardContainer.addView(imageView);
        cardContainer.addView(textView);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 350);
        params.setMargins(8, 8, 8, 8);
        cardContainer.setLayoutParams(params);

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
