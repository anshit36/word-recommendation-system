package org.example.wordrecommender;

import org.springframework.web.bind.annotation.*;
        import java.util.*;

@RestController
@RequestMapping("/recommendation") // Changed to match frontend
public class RecommendationController {

    private final Trie trie;

    public RecommendationController() {
        trie = new Trie();
        String uniqueWordsFilePath = "C:/Users/ashk8/IdeaProjects/wordrecommender/src/main/java/org/example/wordrecommender/English_words.csv";
        String corpusFilePath = "C:/Users/ashk8/IdeaProjects/wordrecommender/src/main/java/org/example/wordrecommender/Corp.txt";
        trie.insertWordsFromCSV(uniqueWordsFilePath);
        trie.processCorpusForNgrams(corpusFilePath);
    }

    @GetMapping("/autocomplete") // Updated endpoint to match frontend fetch
    public Map<String, Double> getSuggestions(@RequestParam String sentence) {
        sentence = sentence.toLowerCase();
        boolean needNextWordSuggestion = sentence.endsWith(" ");
        String[] words = sentence.replaceAll("[^a-zA-Z ]", "").split("\\s+");

        String prefix = "";
        String lastWord = "";
        String secondLastWord = "";

        if (needNextWordSuggestion) {
            if (words.length > 0) {
                lastWord = words[words.length - 1];
                if (words.length > 1) {
                    secondLastWord = words[words.length - 2];
                }
            }
        } else {
            if (words.length > 0) {
                prefix = words[words.length - 1];
                if (words.length > 1) {
                    lastWord = words[words.length - 2];
                }
                if (words.length > 2) {
                    secondLastWord = words[words.length - 3];
                }
            }
        }

        if (secondLastWord.isEmpty()) {
            return trie.getSuggestions(prefix, lastWord);
        } else {
            return trie.getSuggestionsWithTrigram(prefix, secondLastWord, lastWord);
        }
    }
}
