package com.example.incrementum;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
                    toggleTurn(context, topCardsContainer, false);
                    toggleTurn(context, bottomCardsContainer, isNewGame);
                },
                (exception) -> {Log.e("SERVER", "Error in ToggleTurn (fetch): " + exception.getMessage());}
        );
    }

    public void switchTurn(Context context) {
        Activity activity = (Activity) context;

        Player playerOne = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(0);
        Player playerTwo = GameStateRepository.getInstance().getCurrentGameState().getPlayers().get(1);
        String currentTurn = GameStateRepository.getInstance().getCurrentGameState().getCurrentTurn();

        toggleTurn(context, bottomCardsContainer, false);

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

                    int favorableConditionValue=0;
                    int unfavorableConditionValue=0;
                    if (weatherCard!=null){
                        for (Condition x: plantCard.getFavorableConditions()){
                            if (weatherCard.getName().equals(x.getCondition())) {
                                favorableConditionValue = x.getPower() * weatherCard.getLevel();
                                break;
                            }
                        }
                        for (Condition x: plantCard.getUnfavorableConditions()){
                            if (weatherCard.getName().equals(x.getCondition())) {
                                unfavorableConditionValue = x.getPower() * weatherCard.getLevel();
                                break;
                            }
                        }
                    }

                    if (cell.getPlantLevel()<plantCard.getLevel()) {
                        cell.setPlantProgress(cell.getPlantProgress() + 1);
                        if (cell.getPlantProgress() >= (plantCard.getDuration()-favorableConditionValue)) {
                            cell.setPlantLevel(cell.getPlantLevel()+1);
                            cell.setPlantProgress(0);
                            cell.setPlantHP(cell.getPlantLevel());
                        }
                    }

                    cell.setPlantHP(cell.getPlantHP() - unfavorableConditionValue);
                    if (cell.getPlantHP()<1){
                        cell.setPlayerName("");
                        cell.setPlantCardId("");
                        cell.setPlantLevel(0);
                        cell.setPlantProgress(0);
                        cell.setPlantHP(0);
                    }
                    else {
                        if (playerOne.getName().equals(cell.getPlayerName())) playerOne.setPoints(playerOne.getPoints()+cell.getPlantLevel());
                        else playerTwo.setPoints(playerTwo.getPoints()+cell.getPlantLevel());
                    }
                }

                String tag = ((char) ('A' + row -1)) + String.valueOf(col);
                ImageView cellView = activity.getWindow().getDecorView().findViewWithTag(tag);
                ImageManager.setNewImage(context, cell, cellView);
            }
        }
        setStepsCounter(context, playerOne, playerTwo);

        if (currentTurn.equals(playerOne.getName())) GameStateRepository.getInstance().getCurrentGameState().setCurrentTurn(playerTwo.getName());
        else GameStateRepository.getInstance().getCurrentGameState().setCurrentTurn(playerOne.getName());

        MainActivity.emitGameState();
    }

    public void setStepsCounter(Context context, Player playerOne, Player playerTwo){
        Activity activity = (Activity) context;
        TextView bottomPoints = activity.findViewById(R.id.bottom_points);
        TextView topPoints = activity.findViewById(R.id.top_points);

        if (MainActivity.currentPlayerName.equals(playerOne.getName())) {
            topPoints.setText(playerTwo.getName() + " turi " + playerTwo.getPoints() + " taškų.");
            bottomPoints.setText(playerOne.getName() + " turi " + playerOne.getPoints() + " taškų.");
        } else {
            topPoints.setText(playerOne.getName() + " turi " + playerOne.getPoints() + " taškų.");
            bottomPoints.setText(playerTwo.getName() + " turi " + playerTwo.getPoints() + " taškų.");
        }

        stepsCounter = activity.findViewById(R.id.steps_counter);
        stepsCounter.setProgress(stepsCounter.getProgress()+1);

        if (stepsCounter.getProgress() == 50){
            EndGameFragment endGameFragment = EndGameFragment.newInstance();
            endGameFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "EndGameFragment");
        }
    }

    public void toggleTurn(Context context, LinearLayout cardsContainer, boolean isTurn) {
        Activity activity = (Activity) context;
        Button plantsButton = activity.findViewById(R.id.plants_button);
        Button weatherButton = activity.findViewById(R.id.weather_button);
        for (int i = 0; i < cardsContainer.getChildCount(); i++) {
            View card = cardsContainer.getChildAt(i);
            card.setAlpha(isTurn ? 1.0f : 0.5f);
            card.setEnabled(isTurn);
            plantsButton.setEnabled(isTurn);
            weatherButton.setEnabled(isTurn);
        }
    }
}
