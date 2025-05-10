package com.example.incrementum;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ToggleTurn {
    private static LinearLayout topCardsContainer;
    private static LinearLayout bottomCardsContainer;
    private static ProgressBar stepsCounter;

    public void initializeTurn(Context context, boolean isNewGame) {
        Activity activity = (Activity) context;
        topCardsContainer = activity.findViewById(R.id.top_cards_container);
        bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);

        GameStateRepository.getInstance().fetchGameStateById(context,
                GameStateRepository.getInstance().getCurrentGameState().getId(),
                gameState -> {
                    toggleTurn(topCardsContainer, false);
                    toggleTurn(bottomCardsContainer, isNewGame);
                },
                (exception) -> {Log.e("SERVER", "Error in ToggleTurn (fetch): " + exception.getMessage());}
        );
    }

    public boolean isBottomTurn() {
        String currentTurn = GameStateRepository.getInstance().getCurrentGameState().getCurrentTurn();
        String bottomPlayerName = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(1).getName();
        return currentTurn.equals(bottomPlayerName);
    }

    public void switchTurn(Context context) {
        Activity activity = (Activity) context;
        stepsCounter = activity.findViewById(R.id.steps_counter);

        Player playerOne = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(0);
        Player playerTwo = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(1);
        String currentTurn = GameStateRepository.getInstance().getCurrentGameState().getCurrentTurn();
        String nextTurn;
        if (currentTurn.equals(playerOne.getName())) nextTurn = playerTwo.getName();
        else nextTurn = playerOne.getName();
        GameStateRepository.getInstance().getCurrentGameState().setCurrentTurn(nextTurn);

        toggleTurn(bottomCardsContainer, isBottomTurn());

        for (int row = 1; row <= 6; row++) {
            for (int col = 1; col <= 6; col++) {
                Cell cell = GameStateRepository.getInstance().getCurrentGameState().getBoard().get("row" + row).get("column" + col);
                Card plantCard; Card weatherCard = null;
                if (!cell.getWeatherCardId().isEmpty()) {
                    weatherCard = CardsRepository.getInstance().getCardById(cell.getWeatherCardId());
                    if (cell.getWeatherDuration()<weatherCard.getDuration()) cell.setWeatherDuration(cell.getWeatherDuration()+1);
                    else {
                        cell.setWeatherDuration(0);
                        cell.setWeatherCardId("");
                    }
                }
                if (!cell.getPlantCardId().isEmpty()) {
                    plantCard = CardsRepository.getInstance().getCardById(cell.getPlantCardId());
                    if (cell.getPlantLevel()<plantCard.getLevel()) {
                        cell.setPlantProgress(cell.getPlantProgress() + 1);
                        if (cell.getPlantProgress()==plantCard.getDuration()) {
                            cell.setPlantLevel(cell.getPlantLevel()+1);
                            cell.setPlantProgress(0);

                            String tag = ((char) ('A' + row -1)) + String.valueOf(col);
                            ImageView cellView = activity.getWindow().getDecorView().findViewWithTag(tag);
                            ImageManager.setNewImage(context, cell, cellView);
                        }
                    }
                    if (playerOne.getName().equals(cell.getPlayerName())) playerOne.setPoints(playerOne.getPoints()+cell.getPlantLevel());
                    else playerTwo.setPoints(playerTwo.getPoints()+cell.getPlantLevel());
                    TextView bottomPoints = activity.findViewById(R.id.bottom_points);
                    TextView topPoints =  activity.findViewById(R.id.top_points);
                    topPoints.setText(playerOne.getName() + " turi " + playerOne.getPoints() + " taškų.");
                    bottomPoints.setText(playerTwo.getName() + " turi " + playerTwo.getPoints() + " taškų.");
                }
            }
        }

        stepsCounter.setProgress(stepsCounter.getProgress()+1);

        if (stepsCounter.getProgress() == 100){
            return;
        }
        MainActivity.emitGameState();
    }

    public void toggleTurn(LinearLayout cardsContainer, boolean isTurn) {
        for (int i = 0; i < cardsContainer.getChildCount(); i++) {
            View card = cardsContainer.getChildAt(i);
            card.setAlpha(isTurn ? 1.0f : 0.5f);
            card.setEnabled(isTurn);
        }
    }

    public LinearLayout currentPlayerContainer(Context context){
        Activity activity = (Activity) context;
        topCardsContainer = activity.findViewById(R.id.top_cards_container);
        bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);
        if (isBottomTurn()) return bottomCardsContainer;
        else return topCardsContainer;
    }
}
