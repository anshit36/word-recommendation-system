Word Recommendation System
A smart word recommendation system that suggests the most probable next words based on the user's input, using Trie data structure and n-gram language models (bigrams and trigrams).

Features
Real-time suggestions: Get word recommendations as you type
Context-aware: Uses both bigrams and trigrams for better predictions
Probability scoring: Shows likelihood for each suggestion
Responsive UI: Works well on both desktop and mobile devices
Fast autocomplete: Trie-based prefix search for efficient lookups
Technologies Used
Frontend: HTML5, CSS3, JavaScript
Backend: Java with Spring Boot
Data Structures: Trie for efficient prefix search
N-gram Models: Bigram and trigram language models with smoothing
Build Tool: Maven
How It Works
Trie Structure: Stores all valid words for fast prefix-based searching
N-gram Models:
Bigrams track word pairs (e.g., "happy birthday")
Trigrams track word triplets (e.g., "happy birthday to")
Probability Calculation:
Uses frequency counts from the corpus
Applies Laplace smoothing for unseen word combinations
Context Handling:
Uses the last 1-2 words as context for better predictions
Falls back to simpler models when data is sparse
Corpus Flexibility
A default corpus is provided for demonstration purposes in the corpus.txt file, but the system is designed to be corpus-agnostic. You can upload and integrate any other text corpus of your choice for training and generating recommendations, making it highly adaptable to different domains.
