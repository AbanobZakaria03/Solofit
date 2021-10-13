package com.example.mycaloriecalc.models;

public class Serving {
    String desc;
    double weight;

    public Serving(String desc, double weight) {
        this.desc = desc;
        this.weight = weight;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
