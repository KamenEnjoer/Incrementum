package com.example.incrementum;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView;

public class ImageManager {
    public static void setNewImage(Context context, Cell cell, ImageView cellView){
        Card plantCard = CardsRepository.getInstance().getCardById(cell.getPlantCardId());
        Card weatherCard = CardsRepository.getInstance().getCardById(cell.getWeatherCardId());

        int imageResId=0;
        if (plantCard!=null) imageResId = context.getResources().getIdentifier(plantCard.getImageNameByLevel(cell.getPlantLevel()), "drawable", context.getPackageName());
        GradientDrawable borderDrawable = new GradientDrawable();
        if (weatherCard!=null) {
            int borderColor = Color.TRANSPARENT;
            if (weatherCard.getName().equals("Lietus")) {
                borderColor = Color.rgb(60 - weatherCard.getLevel()*20, 210 - weatherCard.getLevel()*20, 255);
            } else if (weatherCard.getName().equals("Saulė")) {
                borderColor = Color.rgb(255, 255 - weatherCard.getLevel()*50, 60 - weatherCard.getLevel()*20);
            }
            borderDrawable.setShape(GradientDrawable.RECTANGLE);
            borderDrawable.setStroke(8, borderColor);
            borderDrawable.setColor(Color.TRANSPARENT);
            borderDrawable.setCornerRadius(5);
        }

        Drawable[] layers = new Drawable[]{
                cellView.getBackground() != null ? cellView.getBackground() : new ColorDrawable(Color.TRANSPARENT),
                plantCard!=null ? context.getDrawable(imageResId) : new ColorDrawable(Color.TRANSPARENT),
                weatherCard!=null ? borderDrawable : new ColorDrawable(Color.TRANSPARENT),
        };
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        cellView.setBackground(layerDrawable);
    }
}
