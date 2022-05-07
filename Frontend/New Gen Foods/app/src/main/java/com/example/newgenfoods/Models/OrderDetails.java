package com.example.newgenfoods.Models;

public class OrderDetails {

    String id, title, qty, addon, notice;

    public OrderDetails(String id, String title, String qty, String addon, String notice) {
        this.title = title;
        this.id = id;
        this.qty = qty;
        this.addon = addon;
        this.notice = notice;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getQty() {
        return qty;
    }

    public String getAddon() {
        return addon;
    }

    public String getNotice() {
        return notice;
    }

}