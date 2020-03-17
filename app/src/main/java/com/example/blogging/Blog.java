package com.example.blogging;

public class Blog {
    private String title;

    public Blog(){

    }
    public Blog(String title, String description, String images) {
        this.title = title;
        this.description = description;
        this.images = images;
    }

    private String description;
    private String images;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }



}
