package com.example.newgenfoods.Classes;

import com.example.newgenfoods.Models.CartItemModel;

import java.util.ArrayList;

public class Cart {

    public static ArrayList<CartItemModel> cartArrayList = new ArrayList<>();

    public ArrayList<CartItemModel> getCartItems() {
        return this.cartArrayList;
    }

    public void addToCart(CartItemModel cartItem) {
        this.cartArrayList.add(cartItem);
    }

    public void removeFromCart(String id) {

        for (int i = 0; i < cartArrayList.size(); i++)
        {
            if (id.equals(cartArrayList.get(i).getId())) {
                cartArrayList.remove(i);
            }
        }

    }

    public void cartClear() {
        this.cartArrayList.clear();
    }

}
