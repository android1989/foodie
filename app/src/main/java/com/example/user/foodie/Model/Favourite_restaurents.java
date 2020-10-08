package com.example.user.foodie.Model;

public class Favourite_restaurents
{
    private String location;
    private String name;
    private String rating;
    private String thumb_image;

    public Favourite_restaurents()
    {

    }

    public Favourite_restaurents(String location, String name, String rating, String thumb_image) {
        this.location = location;
        this.name = name;
        this.rating = rating;
        this.thumb_image = thumb_image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
