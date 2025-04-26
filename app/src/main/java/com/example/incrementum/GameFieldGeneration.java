package com.example.incrementum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class GameFieldGeneration {

    static public void generateGameField(GridLayout gridLayout, Context context) {
        int rowCount = 6;
        int columnCount = 6;
        gridLayout.setRowCount(rowCount);
        gridLayout.setColumnCount(columnCount);
        int totalCells = rowCount * columnCount;
        for (int i = 0; i < totalCells; i++) {
            TextView cell = new TextView(context);
            cell.setBackgroundResource(R.drawable.card_background);
            cell.setGravity(android.view.Gravity.CENTER);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(i % columnCount, 1, 1f);
            params.rowSpec = GridLayout.spec(i / rowCount, 1, 1f);
            params.setMargins(4, 4, 4, 4);

            cell.setLayoutParams(params);
            setupDragAndDropForCell(cell, context);
            gridLayout.addView(cell);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static void setupDragAndDropForCell(View cell, Context context) {
        cell.setOnClickListener(v -> {
            if (v instanceof TextView) {
                TextView textViewCell = (TextView) v;
                String tag = textViewCell.getTag() != null ? textViewCell.getTag().toString() : "Пустая клетка";

                // Создаём и показываем фрагмент
                InfoFragment infoFragment = InfoFragment.newInstance("Состояние клетки:\n" + tag);
                infoFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "cellInfo");
            }
        });

        cell.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setAlpha(0.5f);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(1.0f);
                    return true;

                case DragEvent.ACTION_DROP:
                    String draggedTag = event.getClipData().getItemAt(0).getText().toString();
                    draggedTag = draggedTag.substring(draggedTag.indexOf("_") + 1);
                    boolean a = v instanceof TextView;
                    if (v instanceof TextView) {
                        TextView textViewCell = (TextView) v;
                        String newTag = textViewCell.getTag() != null ? textViewCell.getTag().toString() : "";
                        if (!draggedTag.isEmpty()) newTag += draggedTag;
                        else newTag += "_" + draggedTag;
                        textViewCell.setTag(newTag.trim());

                        boolean alreadyHasBlueBorder = textViewCell.getTag() != null && textViewCell.getTag().toString().contains("oras");
                        Drawable[] layers = new Drawable[]{
                                textViewCell.getBackground() != null ? textViewCell.getBackground() : context.getDrawable(R.drawable.card_background),
                                draggedTag.contains("organizmas") ? context.getDrawable(R.drawable.green_background) : new ColorDrawable(Color.TRANSPARENT),
                                (draggedTag.contains("oras") || alreadyHasBlueBorder) ? context.getDrawable(R.drawable.blue_border) : new ColorDrawable(Color.TRANSPARENT)
                        };
                        LayerDrawable layerDrawable = new LayerDrawable(layers);
                        textViewCell.setBackground(layerDrawable);

                        View draggedView = (View) event.getLocalState();
                        ViewGroup parent = (ViewGroup) draggedView.getParent();
                        if (parent != null) {
                            parent.removeView(draggedView);
                        }

                        ToggleTurn toggleTurn = new ToggleTurn();
                        toggleTurn.switchTurn(v.getContext());
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f);
                    return true;

                default:
                    return false;
            }
        });
    }
}
