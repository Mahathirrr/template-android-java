package com.example.template_project.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Model class representing a flashcard with a front (question) and back (answer)
 */
public class Flashcard implements Serializable {
    private String id;
    private String front;
    private String back;
    private long createdAt;
    private long updatedAt;

    public Flashcard() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Flashcard(String front, String back) {
        this();
        this.front = front != null ? front : "";
        this.back = back != null ? back : "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFront() {
        return front != null ? front : "";
    }

    public void setFront(String front) {
        this.front = front;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getBack() {
        return back != null ? back : "";
    }

    public void setBack(String back) {
        this.back = back;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flashcard flashcard = (Flashcard) o;
        return id.equals(flashcard.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}