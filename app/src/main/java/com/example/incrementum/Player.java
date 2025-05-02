package com.example.incrementum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private String name;
    private int points=0;
    private List<String> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public int getPoints() { return points;}
    public List<String> getCardsIdInHand() { return hand; }

    public void addCardIdToHand(String cardID) { hand.add(cardID); }
    public void removeCardIdFromHand(String cardID) { hand.remove(cardID); }
}
