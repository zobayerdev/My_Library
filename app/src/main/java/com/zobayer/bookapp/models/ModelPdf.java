package com.zobayer.bookapp.models;

public class ModelPdf {

    //variable
    String uid,id,description,categoryId,url,title;
    long timestamp,viewsCount,downloadsCount;
    boolean favorite;

    //empty constractor requires for firebase
    public ModelPdf() {
    }
    //constractor with all params
    public ModelPdf(String uid, String id, String description, String categoryId, String url, String title, long timestamp, long viewsCount, long downloadsCount, boolean favorite) {
        this.uid = uid;
        this.id = id;
        this.description = description;
        this.categoryId = categoryId;
        this.url = url;
        this.title = title;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
        this.downloadsCount = downloadsCount;
        this.favorite = favorite;
    }


    //---- getter and setters---

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(long downloadsCount) {
        this.downloadsCount = downloadsCount;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
