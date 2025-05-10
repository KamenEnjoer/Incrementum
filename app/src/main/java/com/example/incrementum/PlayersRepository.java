package com.example.incrementum;

import java.util.ArrayList;
import java.util.List;

public class PlayersRepository {
    private static PlayersRepository instance;
    private Player playerOne;
    private Player playerTwo;

    private PlayersRepository() {}

    public static PlayersRepository getInstance() {
        if (instance == null) instance = new PlayersRepository();
        return instance;
    }

    public void initializePlayers(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public Player getPlayerOne() { return playerOne; }
    public Player getPlayerTwo() { return playerTwo; }
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(playerOne);
        players.add(playerTwo);
        return players;
    }

    public void setPlayers(Player one, Player two){
        playerOne = one;
        playerTwo = two;
    }
}
