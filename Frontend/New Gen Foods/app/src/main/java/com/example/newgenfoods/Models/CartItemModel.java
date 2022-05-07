package com.example.newgenfoods.Models;

public class CartItemModel {

    Integer qty;
    Double price, total;
    String id, title, category, ingredients, addon, notice;

    public CartItemModel(String id, Integer qty, Double price, Double total, String title, String category, String ingredients, String addon, String notice) {
        this.id = id;
        this.qty = qty;
        this.price = price;
        this.total = total;
        this.title = title;
        this.category = category;
        this.ingredients = ingredients;
        this.addon = addon;
        this.notice = notice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getAddon() {
        return addon;
    }

    public void setAddon(String addon) {
        this.addon = addon;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }
}
