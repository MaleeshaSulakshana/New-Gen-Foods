package com.example.newgenfoods.Models;

public class AddonItems {

    String name;
    Double price;

    public AddonItems(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

}
