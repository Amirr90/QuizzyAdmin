package com.e.quizzyadmin.Model;

public class CategoryModel {
    private String title;
    private String image;
    private boolean isActive;
    private long question;
    private long sets;
    private boolean showOnHomePage;
    private String collectionRef;
    private String id;

    public CategoryModel(String title, String image, boolean isActive, long question, long sets, boolean showOnHomePage, String collectionRef, String id) {
        this.title = title;
        this.image = image;
        this.isActive = isActive;
        this.question = question;
        this.sets = sets;
        this.showOnHomePage = showOnHomePage;
        this.collectionRef = collectionRef;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCollectionRef() {
        return collectionRef;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public boolean isActive() {
        return isActive;
    }

    public long getQuestion() {
        return question;
    }

    public long getSets() {
        return sets;
    }

    public boolean isShowOnHomePage() {
        return showOnHomePage;
    }
}
