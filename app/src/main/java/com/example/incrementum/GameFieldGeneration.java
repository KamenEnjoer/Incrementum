package com.example.incrementum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class GameFieldGeneration {
    static public void generateGameField(GridLayout gridLayout, Context context) {
        gridLayout.removeAllViews();
        int rowCount = 6;
        int columnCount = 6;
        gridLayout.setRowCount(rowCount);
        gridLayout.setColumnCount(columnCount);

        for (int row = 1; row <= 6; row++) {
            for (int col = 1; col <= 6; col++) {
                Cell cell = GameStateRepository.getInstance().getCurrentGameState().getBoard().get("row" + row).get("column" + col);
                ImageView cellView = new ImageView(context);
                ImageManager.setNewImage(context, cell, cellView);
                cellView.setClipToOutline(true);
                cellView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.columnSpec = GridLayout.spec(col-1, 1, 1f);
                params.rowSpec = GridLayout.spec(row-1, 1, 1f);
                params.setMargins(4, 4, 4, 4);
                cellView.setLayoutParams(params);

                char rowChar = (char) ('A' + (row-1));
                cellView.setTag(rowChar + String.valueOf(col));

                setupDragAndDropForCell(cellView, context);
                gridLayout.addView(cellView);
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static void setupDragAndDropForCell(View cell, Context context) {
        cell.setOnClickListener(v -> {
            if (v instanceof ImageView) {
                ImageView viewCell = (ImageView) v;
                CellInfoFragment cellInfoFragment = CellInfoFragment.newInstance(viewCell.getTag().toString());
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
                    if (v instanceof ImageView) {
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
                                    if (view instanceof ImageView) {
                                        ImageView cellView = (ImageView) view;
                                        Cell capturedCell = GameStateRepository.getInstance().getCurrentGameState().getBoard().get("row" + (r+1)).get("column" + (c+1));

                                        if (card.getType().equals("organizmas") && !capturedCell.getPlantCardId().isEmpty()) return true;
                                        else if (card.getType().equals("oras")) {
                                            capturedCell.setWeatherCardId(card.getId());
                                            capturedCell.setWeatherDuration(0);
                                        }
                                        else {
                                            capturedCell.setPlayerName(GameStateRepository.getInstance().getCurrentGameState().getCurrentTurn());
                                            capturedCell.setPlantCardId(card.getId());
                                            capturedCell.setPlantLevel(1);
                                            capturedCell.setPlantProgress(0);
                                            capturedCell.setPlantHP(1);
                                        }
                                        ImageManager.setNewImage(context, capturedCell, cellView);
                                    }
                                }
                            }
                        }

                        ToggleTurn toggleTurn = new ToggleTurn();
                        View draggedView = (View) event.getLocalState();
                        ViewGroup parent = (ViewGroup) draggedView.getParent();
                        if (parent != null) {
                            GameState gameState = GameStateRepository.getInstance().getCurrentGameState();
                            Player player;
                            if (MainActivity.currentPlayerName.equals(gameState.getPlayers().get(0).getName())) player = gameState.getPlayers().get(0);
                            else player = gameState.getPlayers().get(1);
                            for (String x: player.getCardsIdInHand())
                            {
                                if (card.getId().equals(x)) {
                                    player.removeCardIdFromHand(x);
                                    break;
                                }
                            }
                            parent.removeView(draggedView);
                        }
                        toggleTurn.switchTurn(v.getContext());
                    }
                    return true;

                default:
                    return false;
            }
        });
    }
}