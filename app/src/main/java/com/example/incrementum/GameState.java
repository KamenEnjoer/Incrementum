package com.example.incrementum;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;
import java.util.List;

public class GameState implements Serializable {
    @SerializedName("_id")
    private String id;
    private Map<String, Map<String, Cell>> board;
    private List<Player> players;
    private String currentTurn="";
    private int connectedPlayers;

    public void setId(String id) {this.id = id;}
    public void setBoard(Map<String, Map<String, Cell>> board) {this.board= board;}
    public void setPlayers(List<Player> players) {this.players =  players;}
    public void setCurrentTurn(String currentTurn) {this.currentTurn = currentTurn;}

    public Map<String, Map<String, Cell>> getBoard() {return board;}
    public List<Player> getPlayers() {return players;}
    public String getCurrentTurn() {return currentTurn;}
    public String getId() {return id;}

    public int getConnectedPlayers() {return connectedPlayers;}
    public void setConnectedPlayers(int connectedPlayers) {this.connectedPlayers = connectedPlayers;}
}
