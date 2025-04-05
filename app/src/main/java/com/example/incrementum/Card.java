package com.example.incrementum;

public class Card {
    private String _id;
    private String name;
    private String description;
    private String type;
    private int level;
    private int duration;
    private Integer square;

    public Card(String name, String description, String type, int level, int duration, Integer square) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.level = level;
        this.duration = duration;
        this.square = square;
    }

    public String getId() {return _id;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getType() {return type;}
    public int getLevel() {return level;}
    public int getDuration() {return duration;}
    public Integer getSquare() {return square;}
}
