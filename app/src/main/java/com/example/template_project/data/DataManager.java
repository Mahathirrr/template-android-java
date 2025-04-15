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

    // Define type tokens as static fields to avoid anonymous inner class issues
    private static final Type DECK_LIST_TYPE = new TypeToken<List<Deck>>(){}.getType();
    private static final Type QUIZ_RESULT_LIST_TYPE = new TypeToken<List<QuizResult>>(){}.getType();
    
    private void loadData() {
        // Initialize empty collections first to avoid null references
        decks = new ArrayList<>();
        quizResults = new ArrayList<>();
        
        try {
            // Load decks
            String decksJson = sharedPreferences.getString(KEY_DECKS, "");
            if (decksJson != null && !decksJson.isEmpty()) {
                List<Deck> loadedDecks = gson.fromJson(decksJson, DECK_LIST_TYPE);
                if (loadedDecks != null) {
                    decks = loadedDecks;
                }
            }
    
            // Load quiz results
            String resultsJson = sharedPreferences.getString(KEY_QUIZ_RESULTS, "");
            if (resultsJson != null && !resultsJson.isEmpty()) {
                List<QuizResult> loadedResults = gson.fromJson(resultsJson, QUIZ_RESULT_LIST_TYPE);
                if (loadedResults != null) {
                    quizResults = loadedResults;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading data: " + e.getMessage());
            // Ensure we have empty collections if loading fails
            decks = new ArrayList<>();
            quizResults = new ArrayList<>();
        }
    }

    private void saveData() {
        try {
            // Ensure collections are not null before saving
            if (decks == null) {
                decks = new ArrayList<>();
            }
            
            if (quizResults == null) {
                quizResults = new ArrayList<>();
            }
            
            // Create editor and save data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            
            // Convert to JSON and save
            String decksJson = gson.toJson(decks, DECK_LIST_TYPE);
            editor.putString(KEY_DECKS, decksJson);
            
            String resultsJson = gson.toJson(quizResults, QUIZ_RESULT_LIST_TYPE);
            editor.putString(KEY_QUIZ_RESULTS, resultsJson);
            
            // Apply changes
            editor.apply();
            
            Log.d(TAG, "Data saved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error saving data: " + e.getMessage());
        }
    }

    // Deck operations
    public List<Deck> getAllDecks() {
        if (decks == null) {
            decks = new ArrayList<>();
        }
        return new ArrayList<>(decks);
    }

    public Deck getDeckById(String deckId) {
        if (decks == null) {
            decks = new ArrayList<>();
            return null;
        }
        
        if (deckId == null) {
            return null;
        }
        
        for (Deck deck : decks) {
            if (deck != null && deckId.equals(deck.getId())) {
                return deck;
            }
        }
        return null;
    }

    public void addDeck(Deck deck) {
        if (decks == null) {
            decks = new ArrayList<>();
        }
        
        if (deck != null) {
            decks.add(deck);
            saveData();
        }
    }

    public void updateDeck(Deck updatedDeck) {
        if (decks == null || updatedDeck == null) {
            return;
        }
        
        String updatedDeckId = updatedDeck.getId();
        if (updatedDeckId == null) {
            return;
        }
        
        for (int i = 0; i < decks.size(); i++) {
            Deck deck = decks.get(i);
            if (deck != null && updatedDeckId.equals(deck.getId())) {
                decks.set(i, updatedDeck);
                saveData();
                return;
            }
        }
    }

    public void deleteDeck(String deckId) {
        if (decks == null || deckId == null) {
            return;
        }
        
        for (int i = 0; i < decks.size(); i++) {
            Deck deck = decks.get(i);
            if (deck != null && deckId.equals(deck.getId())) {
                decks.remove(i);
                saveData();
                return;
            }
        }
    }

    // Card operations
    public void addCardToDeck(String deckId, Flashcard card) {
        if (deckId == null || card == null) {
            return;
        }
        
        Deck deck = getDeckById(deckId);
        if (deck != null) {
            deck.addCard(card);
            saveData();
        }
    }

    public void updateCard(String deckId, Flashcard updatedCard) {
        if (deckId == null || updatedCard == null) {
            return;
        }
        
        Deck deck = getDeckById(deckId);
        if (deck != null) {
            List<Flashcard> cards = deck.getCards();
            if (cards != null) {
                String updatedCardId = updatedCard.getId();
                if (updatedCardId != null) {
                    for (int i = 0; i < cards.size(); i++) {
                        Flashcard card = cards.get(i);
                        if (card != null && updatedCardId.equals(card.getId())) {
                            cards.set(i, updatedCard);
                            saveData();
                            return;
                        }
                    }
                }
            }
        }
    }

    public void deleteCard(String deckId, String cardId) {
        if (deckId == null || cardId == null) {
            return;
        }
        
        Deck deck = getDeckById(deckId);
        if (deck != null) {
            List<Flashcard> cards = deck.getCards();
            if (cards != null) {
                for (int i = 0; i < cards.size(); i++) {
                    Flashcard card = cards.get(i);
                    if (card != null && cardId.equals(card.getId())) {
                        cards.remove(i);
                        saveData();
                        return;
                    }
                }
            }
        }
    }

    // Quiz operations
    public List<Flashcard> getRandomizedCards(String deckId) {
        if (deckId == null) {
            return new ArrayList<>();
        }
        
        Deck deck = getDeckById(deckId);
        if (deck != null && deck.getCards() != null && !deck.getCards().isEmpty()) {
            List<Flashcard> randomizedCards = new ArrayList<>(deck.getCards());
            Collections.shuffle(randomizedCards);
            return randomizedCards;
        }
        return new ArrayList<>();
    }

    public void saveQuizResult(QuizResult result) {
        if (result == null) {
            return;
        }
        
        if (quizResults == null) {
            quizResults = new ArrayList<>();
        }
        
        quizResults.add(result);
        saveData();
    }

    public List<QuizResult> getQuizResultsForDeck(String deckId) {
        List<QuizResult> results = new ArrayList<>();
        
        if (deckId == null || quizResults == null) {
            return results;
        }
        
        for (QuizResult result : quizResults) {
            if (result != null && deckId.equals(result.getDeckId())) {
                results.add(result);
            }
        }
        return results;
    }
}