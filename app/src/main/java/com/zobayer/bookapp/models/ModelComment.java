package com.zobayer.bookapp.models;

public class ModelComment {

    //variables
    String id, bookId,comment,uid,timestamp;

    //contructor, empty required by firebase

    public ModelComment() {
    }

    //constructor

    public ModelComment(String id, String bookId, String comment, String uid, String timestamp) {
        this.id = id;
        this.bookId = bookId;
        this.comment = comment;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    //setter and getter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
