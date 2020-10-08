package com.example.user.foodie.Model;

public class Favourite_items
{
   private String image;
   private String name;
   private String restaurent_name;
   private String item_key;

    public Favourite_items(String image, String name, String restaurent_name,String item_key) {
        this.image = image;
        this.name = name;
        this.restaurent_name = restaurent_name;
        this.item_key = item_key;
    }

    public Favourite_items()
    {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestaurent_name() {
        return restaurent_name;
    }

    public void setRestaurent_name(String restaurent_name)
    {
        this.restaurent_name = restaurent_name;
    }

    public void setItem_key(String item_key)
    {
        this.item_key = item_key;
    }

    public String getItem_key()
    {
        return item_key;
    }
}
