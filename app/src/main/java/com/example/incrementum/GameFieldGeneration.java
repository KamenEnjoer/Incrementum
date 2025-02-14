package com.example.incrementum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipDescription;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

public class GameFieldGeneration {

    static public void generateGameField(GridLayout gridLayout, Context context) {
        gridLayout.setRowCount(6);
        gridLayout.setColumnCount(6);

        int totalCells = 6 * 6;
        for (int i = 0; i < totalCells; i++) {
            TextView cell = new TextView(context);
            cell.setBackgroundResource(R.drawable.card_background);
            cell.setText("");
            cell.setGravity(android.view.Gravity.CENTER);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(i % 6, 1, 1f);
            params.rowSpec = GridLayout.spec(i / 6, 1, 1f);
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

                    if (v instanceof TextView) {
                        if (v.getBackground() != null && cell.getBackground().getConstantState() != v.getContext().getDrawable(R.drawable.card_background).getConstantState()) {
                            return false;
                        }

                        if (draggedTag.startsWith("Top")) {
                            v.setBackgroundColor(v.getContext().getResources().getColor(android.R.color.holo_red_light));
                            ((MainActivity) v.getContext()).endTurn(true);
                        } else if (draggedTag.startsWith("Bottom")) {
                            v.setBackgroundColor(v.getContext().getResources().getColor(android.R.color.holo_orange_light));
                            ((MainActivity) v.getContext()).endTurn(false);
                        }

                        View draggedView = (View) event.getLocalState();
                        ViewGroup parent = (ViewGroup) draggedView.getParent();
                        if (parent != null) {
                            parent.removeView(draggedView);
                        }
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
