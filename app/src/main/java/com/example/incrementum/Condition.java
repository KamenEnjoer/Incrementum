package com.example.incrementum;

public class Condition {
    private String condition;
    private int power;

    public Condition(String condition, int power){
        this.condition = condition;
        this.power = power;
    }

    public int getPower() {return power;}
    public String getCondition() {return condition;}

    public void setPower(int power) {this.power = power;}
    public void setCondition(String condition) {this.condition = condition;}
}
