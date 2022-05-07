package com.example.newgenfoods.Models;

public class MenuItems {

    String id, title, price, ingredients, status;

    public MenuItems(String id, String title, String price, String ingredients,String status) {
        this.title = title;
        this.id = id;
        this.price = price;
        this.ingredients = ingredients;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getStatus() {
        return status;
    }

}
