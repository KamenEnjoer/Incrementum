package com.example.incrementum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipDescription;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

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
            cell.setText("");
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
        cell.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setAlpha(0.5f);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(1.0f);
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String draggedTag = item.getText().toString();

                    Log.d("GameField", "Dragged tag: " + draggedTag);

                    if (v instanceof TextView) {
                        TextView textViewCell = (TextView) v;
                        // Храним текущие состояния фона и рамки
                        boolean hasGreenBackground = textViewCell.getTag() != null && textViewCell.getTag().equals("green");
                        boolean hasBlueBorder = textViewCell.getTag() != null && textViewCell.getTag().equals("blue");

                        // Добавляем зелёный фон, если не установлен
                        if (draggedTag.contains("green") && !hasGreenBackground) {
                            textViewCell.setBackgroundColor(v.getContext().getColor(android.R.color.holo_green_light)); // Зеленый цвет
                            textViewCell.setTag("green"); // Обновляем тег ячейки
                        }

                        // Добавляем синюю рамку, если она ещё не установлена
                        if (draggedTag.contains("blue") && !hasBlueBorder) {
                            textViewCell.setBackground(v.getContext().getDrawable(R.drawable.blue_border)); // Синяя рамка
                            textViewCell.setTag("blue"); // Обновляем тег ячейки
                        }

                        // Удаляем карточку из контейнера
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
