package com.example.newgenfoods.Models;

public class OrderItems {

    String orderNo, status;

    public OrderItems(String orderNo, String status) {
        this.orderNo = orderNo;
        this.status = status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getStatus() {
        return status;
    }

}
