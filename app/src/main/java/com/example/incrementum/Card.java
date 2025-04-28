package com.example.incrementum;

import java.io.Serializable;

public class Card implements Serializable {
    private String _id;
    private String name;
    private String description;
    private String type;
    private int level;
    private int duration;
    private int square;
    private String imageName;

    public Card(String name, String description, String type, int level, int duration, int square) {
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
    public int getSquare() {return square;}
    public String getImageName(){
        if (type.equals("oras")) imageName = "w0_" + _id;
        else imageName = "p0_" + _id;
        return imageName;
    }
}
