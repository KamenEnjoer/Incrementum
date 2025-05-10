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
    private static boolean isBottomTurn = true;
    private static LinearLayout topCardsContainer;
    private static LinearLayout bottomCardsContainer;
    private static ProgressBar stepsCounter;

    public void initializeTurn(Context context) {
        Activity activity = (Activity) context;
        topCardsContainer = activity.findViewById(R.id.top_cards_container);
        bottomCardsContainer = activity.findViewById(R.id.bottom_cards_container);

        GameStateRepository.getInstance().fetchGameStateById(context,
                GameStateRepository.getInstance().getCurrentGameState().getId(),
                gameState -> {
                    toggleTurn(topCardsContainer, false);
                    toggleTurn(bottomCardsContainer, true);
                },
                (exception) -> {Log.e("SERVER", "Error in ToggleTurn (fetch): " + exception.getMessage());}
        );
    }

    public void switchTurn(Context context) {
        Activity activity = (Activity) context;
        stepsCounter = activity.findViewById(R.id.steps_counter);
        Player playerOne = PlayersRepository.getInstance().getPlayerOne();
        Player playerTwo = PlayersRepository.getInstance().getPlayerTwo();
        isBottomTurn = !isBottomTurn;
        if (isBottomTurn) GameStateRepository.getInstance().getCurrentGameState().setCurrentTurn(playerOne.getName());
        else GameStateRepository.getInstance().getCurrentGameState().setCurrentTurn(playerTwo.getName());


        toggleTurn(topCardsContainer, !isBottomTurn);
        toggleTurn(bottomCardsContainer, isBottomTurn);

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

        GameStateRepository.getInstance().getCurrentGameState().setPlayers(PlayersRepository.getInstance().getPlayers());
        if (stepsCounter.getProgress() == 100){
            return;
        }
        MainActivity.emitGameState();
    }

    private void toggleTurn(LinearLayout cardsContainer, boolean isTurn) {
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
        if (isBottomTurn) return bottomCardsContainer;
        else return topCardsContainer;
    }
}
