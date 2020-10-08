package com.example.user.foodie.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Items {
    private String name;
    private String price;
    private String image;
    private String descriptions;
    private String favourite;
    private String item_key;

    public Items()
    {

    }


    public void setItem_key(String item_key) {
        this.item_key = item_key;
    }

    public String getItem_key() {
        return item_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

}