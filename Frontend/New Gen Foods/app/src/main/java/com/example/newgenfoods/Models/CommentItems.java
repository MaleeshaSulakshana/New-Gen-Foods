package com.example.newgenfoods.Models;

public class CommentItems {

    String comment, name;

    public CommentItems(String name, String comment) {
        this.comment = comment;
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

}
