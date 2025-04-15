# FlashCards App

A simple yet powerful flashcard application for Android that helps users create and manage study cards for vocabulary, definitions, and more.

## Features

- **Dark Theme**: Modern and eye-friendly dark theme for comfortable studying in any lighting condition
- **Deck Management**: Create, edit, and organize flashcard decks by topic
- **Flashcard Creation**: Add cards with questions (front) and answers (back)
- **Quiz Mode**: Test your knowledge with randomized questions
- **Card Flipping**: Interactive card flipping animation to reveal answers
- **Progress Tracking**: Track your quiz performance with results summary

## Technical Details

- **Language**: Java
- **Target SDK**: 34
- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Architecture**: Single-activity with RecyclerViews for deck and card lists
- **Data Storage**: SharedPreferences with GSON serialization
- **UI Components**: Material Design components for a modern look and feel

## Project Structure

- **Model Classes**:
  - `Deck.java`: Represents a collection of flashcards
  - `Flashcard.java`: Represents a single flashcard with front and back content
  - `QuizResult.java`: Stores quiz attempt results

- **Activities**:
  - `MainActivity.java`: Main screen showing deck list
  - `DeckDetailActivity.java`: Shows cards in a deck and allows card management
  - `QuizActivity.java`: Quiz mode for testing knowledge

- **Adapters**:
  - `DeckAdapter.java`: RecyclerView adapter for deck list
  - `CardAdapter.java`: RecyclerView adapter for card list

- **Data Management**:
  - `DataManager.java`: Singleton class for data persistence and retrieval

## How to Use

1. **Create a Deck**: Tap the "Create New Deck" button on the main screen
2. **Add Cards**: Open a deck and tap the floating action button to add cards
3. **Study Cards**: Browse through cards and tap the flip icon to reveal answers
4. **Quiz Mode**: Tap the "Start Quiz" button in a deck to test your knowledge
5. **Track Progress**: View your quiz results after completing a quiz session

## Future Enhancements

- Cloud synchronization
- Image support for cards
- Advanced statistics and learning algorithms
- Export/import functionality
- Multiple choice quiz mode
- Spaced repetition system