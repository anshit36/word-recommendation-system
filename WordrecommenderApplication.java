package org.example.wordrecommender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WordrecommenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordrecommenderApplication.class, args);
        System.out.println("Word Recommendation System is running...");
    }
}
