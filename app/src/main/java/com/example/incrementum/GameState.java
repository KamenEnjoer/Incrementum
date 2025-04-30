package com.example.incrementum;

import java.io.Serializable;
import java.util.Map;
import java.util.List;

public class GameState implements Serializable {
    private Map<String, Map<String, Cell>> board;
    private List<Player> players;
    private String currentTurn="";

    public void setBoard(Map<String, Map<String, Cell>> board) {this.board= board;}
    public void setPlayers(List<Player> players) {this.players =  players;}
    public void setCurrentTurn(String currentTurn) {this.currentTurn = currentTurn;}

    public Map<String, Map<String, Cell>> getBoard() {return board;}
    public List<Player> getPlayers() {return players;}
    public String getCurrentTurn() {return currentTurn;}
}
