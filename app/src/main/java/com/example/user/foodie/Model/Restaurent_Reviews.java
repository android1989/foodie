package com.example.user.foodie.Model;

public class Restaurent_Reviews
{
    private String name;
    private String review_text;
    private String thumb_image;
    private Long time_ago;
    private String rating;

    public Restaurent_Reviews()
    {
        //default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public Long getTime_ago() {
        return time_ago;
    }

    public void setTime_ago(Long time_ago) {
        this.time_ago = time_ago;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
