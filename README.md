# Word Recommendation System

A smart word recommendation system that suggests the most probable next words based on the user's input, using Trie data structure and n-gram language models (bigrams and trigrams).

## Features

- **Real-time suggestions**: Get word recommendations as you type
- **Context-aware**: Uses both bigrams and trigrams for better predictions
- **Probability scoring**: Shows likelihood for each suggestion
- **Responsive UI**: Works well on both desktop and mobile devices
- **Fast autocomplete**: Trie-based prefix search for efficient lookups

## Technologies Used

- **Frontend**: HTML5, CSS3, JavaScript
- **Backend**: Java with Spring Boot
- **Data Structures**: Trie for efficient prefix search
- **N-gram Models**: Bigram and trigram language models with smoothing
- **Build Tool**: Maven

## How It Works

1. **Trie Structure**: Stores all valid words for fast prefix-based searching
2. **N-gram Models**:
   - Bigrams track word pairs (e.g., "happy birthday")
   - Trigrams track word triplets (e.g., "happy birthday to")
3. **Probability Calculation**:
   - Uses frequency counts from the corpus
   - Applies Laplace smoothing for unseen word combinations
4. **Context Handling**:
   - Uses the last 1-2 words as context for better predictions
   - Falls back to simpler models when data is sparse

## Installation

1. **Prerequisites**:
   - Java JDK 11+
   - Maven 3.6+
   - Modern web browser

2. **Setup**:
   ```bash
   git clone https://github.com/yourusername/word-recommendation-system.git
   cd word-recommendation-system
   mvn clean install
