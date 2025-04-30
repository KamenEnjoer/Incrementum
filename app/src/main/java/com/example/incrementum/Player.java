package com.example.incrementum;

import java.io.Serializable;
import java.util.List;

public class Player implements Serializable {
    private String name;
    private List<String> cardsIdInHand;

    public String getName() { return name; }
    public List<String> getCardsIdInHand() { return cardsIdInHand; }
}
