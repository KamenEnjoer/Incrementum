package com.example.incrementum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
            char row = (char) ('A' + (i / rowCount));
            cell.setTag(row + String.valueOf((i % columnCount)+1));
            setupDragAndDropForCell(cell, context);
            gridLayout.addView(cell);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static void setupDragAndDropForCell(View cell, Context context) {
        cell.setOnClickListener(v -> {
            if (v instanceof TextView) {
                TextView textViewCell = (TextView) v;
                CellInfoFragment cellInfoFragment = CellInfoFragment.newInstance(textViewCell.getTag().toString());
                cellInfoFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "cellInfo");
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
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f);
                    return true;

                case DragEvent.ACTION_DROP:
                    if (v instanceof TextView) {
                        String draggedTag = event.getClipData().getItemAt(0).getText().toString();
                        draggedTag = draggedTag.substring(draggedTag.indexOf("*") + 1);
                        Card card = CardsRepository.getInstance().getCardById(draggedTag);
                        int areaSize = 1;
                        if (card.getType().equals("oras")) areaSize = card.getSquare();
                        int halfArea = areaSize / 2;

                        GridLayout gridLayout = (GridLayout) v.getParent();
                        int row = v.getTag().toString().charAt(0) - 'A';
                        int column = Character.getNumericValue(v.getTag().toString().charAt(1)) - 1;

                        int startRow, endRow, startCol, endCol;
                        if (areaSize % 2 == 0) {
                            startRow = row;
                            startCol = column;
                            endRow = row + areaSize - 1;
                            endCol = column + areaSize - 1;
                        } else {
                            startRow = row - halfArea;
                            startCol = column - halfArea;
                            endRow = row + halfArea;
                            endCol = column + halfArea;
                        }

                        for (int r = startRow; r <= endRow; r++) {
                            for (int c = startCol; c <= endCol; c++) {
                                if (r >= 0 && r < gridLayout.getRowCount() && c >= 0 && c < gridLayout.getColumnCount()) {
                                    int cellIndex = r * gridLayout.getColumnCount() + c;
                                    View view = gridLayout.getChildAt(cellIndex);
                                    if (view instanceof TextView) {
                                        TextView neighborCell = (TextView) view;

                                        String newTag = neighborCell.getTag().toString();
                                        newTag += "_" + card.getType() + "*" + card.getId();
                                        neighborCell.setTag(newTag.trim());

                                        boolean alreadyHasBlueBorder = neighborCell.getTag() != null && neighborCell.getTag().toString().contains("oras");
                                        Drawable[] layers = new Drawable[]{
                                                neighborCell.getBackground() != null ? neighborCell.getBackground() : context.getDrawable(R.drawable.card_background),
                                                card.getType().equals("organizmas") ? context.getDrawable(R.drawable.green_background) : new ColorDrawable(Color.TRANSPARENT),
                                                (card.getType().equals("oras") || alreadyHasBlueBorder) ? context.getDrawable(R.drawable.blue_border) : new ColorDrawable(Color.TRANSPARENT)
                                        };
                                        LayerDrawable layerDrawable = new LayerDrawable(layers);
                                        neighborCell.setBackground(layerDrawable);
                                    }
                                }
                            }
                        }

                        View draggedView = (View) event.getLocalState();
                        ViewGroup parent = (ViewGroup) draggedView.getParent();
                        if (parent != null) {
                            parent.removeView(draggedView);
                        }

                        ToggleTurn toggleTurn = new ToggleTurn();
                        toggleTurn.switchTurn(v.getContext());
                    }
                    return true;

                default:
                    return false;
            }
        });
    }
}
