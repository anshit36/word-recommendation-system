package org.example.wordrecommender;

import java.io.*;
        import java.util.*;
        import java.util.stream.*;

class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEndOfWord;

    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
}

class Trie {
    private TrieNode root;
    private Map<String, List<String>> bigramMap;
    private Map<String, Map<String, Integer>> bigramCountMap;
    // New maps for trigrams
    private Map<String, List<String>> trigramMap;
    private Map<String, Map<String, Integer>> trigramCountMap;

    public Trie() {
        root = new TrieNode();
        bigramMap = new HashMap<>();
        bigramCountMap = new HashMap<>();
        trigramMap = new HashMap<>();
        trigramCountMap = new HashMap<>();
    }

    public static void main(String[] args) {
        Trie trie = new Trie();

        String uniqueWordsFilePath = "C:/Users/ashk8/IdeaProjects/wordrecommender/src/main/java/org/example/wordrecommender/English_words.csv";
        trie.insertWordsFromCSV(uniqueWordsFilePath);

        String corpusFilePath = "C:/Users/ashk8/IdeaProjects/wordrecommender/src/main/java/org/example/wordrecommender/Corp.txt";
        trie.processCorpusForNgrams(corpusFilePath);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a sentence for auto-completion: ");
        String inputSentence = scanner.nextLine().trim().toLowerCase();

        // Split the sentence into words
        String[] words = inputSentence.replaceAll("[^a-zA-Z ]", "").split("\\s+");

        String prefix = "";
        String lastWord = "";
        String secondLastWord = "";

        // Determine prefix and context words
        if (words.length > 0) {
            prefix = words[words.length - 1];
            if (words.length > 1) {
                lastWord = words[words.length - 2];
            }
            if (words.length > 2) {
                secondLastWord = words[words.length - 3];
            }
        }

        Map<String, Double> recommendations;
        if (secondLastWord.isEmpty()) {
            recommendations = trie.getSuggestions(prefix, lastWord);
        } else {
            recommendations = trie.getSuggestionsWithTrigram(prefix, secondLastWord, lastWord);
        }

        if (!recommendations.isEmpty()) {
            System.out.println("Top 5 auto-completion suggestions for '" + prefix + "': ");
            recommendations.forEach((word, probability) ->
                    System.out.printf("%s (Probability: %.2f)\n", word, probability));
        } else {
            System.out.println("No words found with the prefix '" + prefix + "'");
        }
        scanner.close();
    }

    public void insertWordsFromCSV(String fileName) {
        System.out.println("Reading unique words from CSV file: " + fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty()) {
                    insert(word);
                }
            }
            System.out.println("Unique words successfully inserted into the Trie.");
        } catch (IOException e) {
            System.out.println("Error reading the CSV file: " + e.getMessage());
        }
    }

    public void processCorpusForNgrams(String fileName) {
        System.out.println("Processing corpus for n-grams: " + fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String previousWord = "";
            String secondPreviousWord = "";

            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        if (!previousWord.isEmpty()) {
                            addBigram(previousWord, word);
                        }
                        if (!previousWord.isEmpty() && !secondPreviousWord.isEmpty()) {
                            addTrigram(secondPreviousWord, previousWord, word);
                        }
                        secondPreviousWord = previousWord;
                        previousWord = word;
                    }
                }
            }
            System.out.println("N-gram processing completed.");
        } catch (IOException e) {
            System.out.println("Error reading the corpus file: " + e.getMessage());
        }
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode());
            node = node.children.get(ch);
        }
        node.isEndOfWord = true;
    }

    private void addBigram(String firstWord, String secondWord) {
        bigramMap.putIfAbsent(firstWord, new ArrayList<>());
        bigramCountMap.putIfAbsent(firstWord, new HashMap<>());

        if (!bigramMap.get(firstWord).contains(secondWord)) {
            bigramMap.get(firstWord).add(secondWord);
        }

        bigramCountMap.get(firstWord).put(secondWord,
                bigramCountMap.get(firstWord).getOrDefault(secondWord, 0) + 1);
    }

    private void addTrigram(String firstWord, String secondWord, String thirdWord) {
        String key = firstWord + " " + secondWord;
        trigramMap.putIfAbsent(key, new ArrayList<>());
        trigramCountMap.putIfAbsent(key, new HashMap<>());

        if (!trigramMap.get(key).contains(thirdWord)) {
            trigramMap.get(key).add(thirdWord);
        }

        trigramCountMap.get(key).put(thirdWord,
                trigramCountMap.get(key).getOrDefault(thirdWord, 0) + 1);
    }

    public Map<String, Double> getSuggestions(String prefix, String precedingWord) {
        List<String> suggestions = search(prefix);

        if (suggestions.isEmpty()) {
            return Collections.emptyMap();
        }

        if (precedingWord.isEmpty()) {
            return suggestions.stream()
                    .collect(Collectors.toMap(
                            word -> word,
                            word -> 1.0,
                            (v1, v2) -> v1,
                            LinkedHashMap::new
                    ));
        }

        return calculateProbabilities(precedingWord, suggestions).entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Double> getSuggestionsWithTrigram(String prefix, String secondLastWord, String lastWord) {
        List<String> suggestions = search(prefix);

        if (suggestions.isEmpty()) {
            return Collections.emptyMap();
        }

        if (lastWord.isEmpty() || secondLastWord.isEmpty()) {
            return getSuggestions(prefix, lastWord);
        }

        return calculateTrigramProbabilities(secondLastWord, lastWord, suggestions).entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    public List<String> search(String prefix) {
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return Collections.emptyList();
            }
            node = node.children.get(ch);
        }
        return autocomplete(node, prefix);
    }

    private List<String> autocomplete(TrieNode node, String prefix) {
        List<String> words = new ArrayList<>();
        if (node.isEndOfWord) {
            words.add(prefix);
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            words.addAll(autocomplete(entry.getValue(), prefix + entry.getKey()));
        }
        return words;
    }

    private Map<String, Double> calculateProbabilities(String precedingWord, List<String> suggestions) {
        Map<String, Double> probabilities = new HashMap<>();
        Map<String, Integer> counts = bigramCountMap.getOrDefault(precedingWord, Collections.emptyMap());

        int totalCount = counts.values().stream().mapToInt(Integer::intValue).sum();
        int vocabularySize = bigramMap.size();

        for (String suggestion : suggestions) {
            int countWithSmoothing = counts.getOrDefault(suggestion, 0);
            int denominator = 0;
            if(countWithSmoothing == 0){
                countWithSmoothing = countWithSmoothing + 1;
                denominator = totalCount + vocabularySize;
            }
            else{
                denominator = totalCount;
            }
            double probability = (double) countWithSmoothing / denominator ;
            probabilities.put(suggestion, probability);
        }
        return probabilities;
    }

    private Map<String, Double> calculateTrigramProbabilities(String secondLastWord, String lastWord,
                                                              List<String> suggestions) {
        Map<String, Double> probabilities = new HashMap<>();
        String key = secondLastWord + " " + lastWord;
        Map<String, Integer> counts = trigramCountMap.getOrDefault(key, Collections.emptyMap());

        int totalCount = counts.values().stream().mapToInt(Integer::intValue).sum();
        int vocabularySize = trigramMap.size();

        for (String suggestion : suggestions) {
            // Apply Laplace smoothing for trigrams
            int countWithSmoothing = counts.getOrDefault(suggestion, 0) ;
            int denominator = 0;
            if(countWithSmoothing == 0){
                countWithSmoothing = countWithSmoothing + 1;
                denominator = totalCount + vocabularySize;
            }
            else{
                denominator = totalCount;
            }
            double probability = (double) countWithSmoothing / denominator ;

            // If trigram probability is 0, fall back to bigram
            if (probability == 0) {
                Map<String, Double> bigramProb = calculateProbabilities(lastWord, Collections.singletonList(suggestion));
                probability = bigramProb.getOrDefault(suggestion, 0.0);
            }

            probabilities.put(suggestion, probability);
        }
        return probabilities;
    }
}
