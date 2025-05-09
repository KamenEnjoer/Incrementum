package com.example.incrementum;

import java.io.Serializable;

public class Cell implements Serializable {
    private String playerName="";
    private String plantCardId="";
    private int plantLevel=0;
    private int plantProgress=0;
    private int plantHP=0;
    private String weatherCardId="";
    private int weatherDuration=0;

    public Cell(String playerName, String plantCardId, int plantLevel, int plantProgress, int plantHP, String weatherCardId, int weatherDuration) {
        this.playerName = playerName;
        this.plantCardId = plantCardId;
        this.plantLevel = plantLevel;
        this.plantProgress = plantProgress;
        this.plantHP = plantHP;
        this.weatherCardId  = weatherCardId;
        this.weatherDuration = weatherDuration;
    }

    public String getPlayerName() {return playerName;}
    public String getPlantCardId() {return plantCardId;}
    public int getPlantLevel() {return plantLevel;}
    public int getPlantProgress() {return plantProgress;}
    public String getWeatherCardId() {return weatherCardId;}
    public int getWeatherDuration() {return weatherDuration;}

    public void setPlayerName(String playerName) {this.playerName = playerName;}
    public void setPlantCardId(String plantCardId) {this.plantCardId=plantCardId;}
    public void setPlantLevel(int plantLevel) {this.plantLevel = plantLevel;}
    public void setPlantProgress(int plantProgress) {this.plantProgress = plantProgress;}
    public void setWeatherCardId(String weatherCardId) {this.weatherCardId = weatherCardId;}
    public void setWeatherDuration(int weatherDuration) {this.weatherDuration = weatherDuration;}
}
