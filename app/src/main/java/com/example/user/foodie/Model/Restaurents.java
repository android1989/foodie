package com.example.user.foodie.Model;

import android.content.Context;


public class Restaurents {
    private String name;
    private String location;
    private String rating;
    private String thumb_image;
    private String image;
    private String counter;
    private String email;
    private String phone;
    private String favourite;
    private String reviews;
    private String status;


    public Restaurents(String name, String location, String rating, String thumb_image, String image, String counter, String email, String phone, String favourite, String reviews, String status) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.thumb_image = thumb_image;
        this.image = image;
        this.counter = counter;
        this.email = email;
        this.phone = phone;
        this.favourite = favourite;
        this.reviews = reviews;
        this.status = status;
    }

    public Restaurents()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

