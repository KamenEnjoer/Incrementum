package com.example.incrementum;

import java.util.ArrayList;
import java.util.List;

public class PlayersRepository {
    private static PlayersRepository instance;
    private Player playerOne;
    private Player playerTwo;
    private Player currentPlayer;

    private PlayersRepository() {}

    public static PlayersRepository getInstance() {
        if (instance == null) instance = new PlayersRepository();
        return instance;
    }

    public void initializePlayers(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentPlayer = playerOne;
    }

    public Player getPlayerOne() { return playerOne; }
    public Player getPlayerTwo() { return playerTwo; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(playerOne);
        players.add(playerTwo);
        return players;
    }

    public void switchTurn() {
        if (currentPlayer == playerOne) currentPlayer = playerTwo;
        else currentPlayer = playerOne;
        MainActivity.defaultGameState.setCurrentTurn(currentPlayer.getName());
    }
}
