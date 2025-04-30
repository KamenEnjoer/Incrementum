package com.example.incrementum;

import java.io.Serializable;
import java.util.Map;
import java.util.List;

public class GameState implements Serializable {
    private Map<String, Map<String, Cell>> board;
    private List<Player> players;

    public Map<String, Map<String, Cell>> getBoard() { return board; }
    public List<Player> getPlayers() { return players; }
}
