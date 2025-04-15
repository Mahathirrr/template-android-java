package com.example.template_project.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.template_project.model.Deck;
import com.example.template_project.model.Flashcard;
import com.example.template_project.model.QuizResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class to manage data storage and retrieval for the app
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private static final String PREFS_NAME = "flashcards_prefs";
    private static final String KEY_DECKS = "decks";
    private static final String KEY_QUIZ_RESULTS = "quiz_results";

    private static DataManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private List<Deck> decks;
    private List<QuizResult> quizResults;

    private DataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadData();
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadData() {
        // Load decks
        String decksJson = sharedPreferences.getString(KEY_DECKS, null);
        if (decksJson != null) {
            Type type = new TypeToken<ArrayList<Deck>>() {}.getType();
            decks = gson.fromJson(decksJson, type);
        } else {
            decks = new ArrayList<>();
        }

        // Load quiz results
        String resultsJson = sharedPreferences.getString(KEY_QUIZ_RESULTS, null);
        if (resultsJson != null) {
            Type type = new TypeToken<ArrayList<QuizResult>>() {}.getType();
            quizResults = gson.fromJson(resultsJson, type);
        } else {
            quizResults = new ArrayList<>();
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        // Save decks
        String decksJson = gson.toJson(decks);
        editor.putString(KEY_DECKS, decksJson);
        
        // Save quiz results
        String resultsJson = gson.toJson(quizResults);
        editor.putString(KEY_QUIZ_RESULTS, resultsJson);
        
        editor.apply();
    }

    // Deck operations
    public List<Deck> getAllDecks() {
        return new ArrayList<>(decks);
    }

    public Deck getDeckById(String deckId) {
        for (Deck deck : decks) {
            if (deck.getId().equals(deckId)) {
                return deck;
            }
        }
        return null;
    }

    public void addDeck(Deck deck) {
        decks.add(deck);
        saveData();
    }

    public void updateDeck(Deck updatedDeck) {
        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).getId().equals(updatedDeck.getId())) {
                decks.set(i, updatedDeck);
                saveData();
                return;
            }
        }
    }

    public void deleteDeck(String deckId) {
        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).getId().equals(deckId)) {
                decks.remove(i);
                saveData();
                return;
            }
        }
    }

    // Card operations
    public void addCardToDeck(String deckId, Flashcard card) {
        Deck deck = getDeckById(deckId);
        if (deck != null) {
            deck.addCard(card);
            saveData();
        }
    }

    public void updateCard(String deckId, Flashcard updatedCard) {
        Deck deck = getDeckById(deckId);
        if (deck != null) {
            List<Flashcard> cards = deck.getCards();
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getId().equals(updatedCard.getId())) {
                    cards.set(i, updatedCard);
                    saveData();
                    return;
                }
            }
        }
    }

    public void deleteCard(String deckId, String cardId) {
        Deck deck = getDeckById(deckId);
        if (deck != null) {
            List<Flashcard> cards = deck.getCards();
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getId().equals(cardId)) {
                    cards.remove(i);
                    saveData();
                    return;
                }
            }
        }
    }

    // Quiz operations
    public List<Flashcard> getRandomizedCards(String deckId) {
        Deck deck = getDeckById(deckId);
        if (deck != null && deck.getCards() != null && !deck.getCards().isEmpty()) {
            List<Flashcard> randomizedCards = new ArrayList<>(deck.getCards());
            Collections.shuffle(randomizedCards);
            return randomizedCards;
        }
        return new ArrayList<>();
    }

    public void saveQuizResult(QuizResult result) {
        quizResults.add(result);
        saveData();
    }

    public List<QuizResult> getQuizResultsForDeck(String deckId) {
        List<QuizResult> results = new ArrayList<>();
        for (QuizResult result : quizResults) {
            if (result.getDeckId().equals(deckId)) {
                results.add(result);
            }
        }
        return results;
    }
}