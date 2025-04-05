package com.example.incrementum;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ToggleTurn toggleTurn;
    CardsGeneration cardsGeneration;
    public Button plantsButton;
    public Button weatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plantsButton = findViewById(R.id.plants_button);
        plantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard("organizmas");
            }
        });
        weatherButton = findViewById(R.id.weather_button);
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard("oras");
            }
        });

        GridLayout gameGrid = findViewById(R.id.game_grid);
        GameFieldGeneration.generateGameField(gameGrid, this);

        cardsGeneration = new CardsGeneration();
        cardsGeneration.generateCards(this);
        toggleTurn = new ToggleTurn();
        toggleTurn.initializeTurn(this);
    }

    public void addNewCard(String type) {
        // Создание объекта Card с нужными данными
        Card newCard = new Card(
                "Name of the Card",  // Замени на реальное имя
                "Description of the Card",  // Замени на реальное описание
                type,  // Тип карты (organizmas/oras)
                1,  // Уровень
                3,  // Длительность
                null  // Может быть null, если поле не задано
        );

        // Добавление карточки в контейнер
        cardsGeneration.generateDraggableCards(toggleTurn.currentPlayer(), newCard, this);

        // Переключение хода
        toggleTurn.switchTurn(this);
    }

}