package com.example.template_project.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Model class representing the results of a quiz session
 */
public class QuizResult implements Serializable {
    private String id;
    private String deckId;
    private int totalCards;
    private int correctAnswers;
    private List<String> incorrectCardIds;
    private long timestamp;

    public QuizResult() {
        this.id = UUID.randomUUID().toString();
        this.incorrectCardIds = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }

    public QuizResult(String deckId, int totalCards) {
        this();
        this.deckId = deckId;
        this.totalCards = totalCards;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void incrementCorrectAnswers() {
        this.correctAnswers++;
    }

    public List<String> getIncorrectCardIds() {
        return incorrectCardIds;
    }

    public void setIncorrectCardIds(List<String> incorrectCardIds) {
        this.incorrectCardIds = incorrectCardIds;
    }

    public void addIncorrectCardId(String cardId) {
        if (this.incorrectCardIds == null) {
            this.incorrectCardIds = new ArrayList<>();
        }
        this.incorrectCardIds.add(cardId);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getScorePercentage() {
        if (totalCards == 0) return 0;
        return ((float) correctAnswers / totalCards) * 100;
    }
}