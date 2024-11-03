package com.assistance.Users.service;
import java.sql.Timestamp; // Import the Timestamp class from java.sql package
import java.time.LocalDateTime;
import com.assistance.Users.dto.MessageDTO;
import com.assistance.Users.model.UserInfoModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

import java.io.InputStream;
import java.util.*;

public class CommandProcessorNlp {

    private TimeDetection timeDetection = new TimeDetection();
    private final POSModel posModel;

    // Constructor to load the POS model
    public CommandProcessorNlp() {
        try (InputStream modelIn = getClass().getResourceAsStream("/opennlp-en-ud-ewt-pos-1.1-2.4.0.bin")) {
            assert modelIn != null;
            posModel = new POSModel(modelIn); // Load the POS model from the resource stream
        } catch (Exception e) {
            throw new RuntimeException("Failed to load POS model.", e);
        }
    }

    // Method to process command and return UserInfoModel with concatenated POS sentences
    public UserInfoModel processCommand(MessageDTO messageDTO) {
        String command = messageDTO.getMessage().toLowerCase(); // Retrieve the command message from the DTO
        int userId = messageDTO.getId(); // Get the user ID from the DTO
        UserInfoModel userInfo = new UserInfoModel();
        // Remove "set" from the command and split based on "as"
        String[] words = command.split("\\s+"); // Split message by whitespace into an array of words
        List<String> wordList = Arrays.asList(words); // Convert array to list for easier matching

        String commandPart = "";
        String resultPart = "";

        // Check if both "set" and "as" exist as individual words in the message
        if (wordList.contains("set") && wordList.contains("as")) {

            String[] parts = command.replace("set", "").split("as");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid command format. Use 'set ... as ...'"); // Validate the command format
            }
            commandPart = parts[0] != null ? parts[0].trim() : ""; // Extract and trim the command part

            resultPart = parts[1] != null ? parts[1].trim() : ""; // Extract and trim the result part
            userInfo.setResponse(resultPart); // Set the resulting response from processing

        } else{
            commandPart = command.replace("remind me that", ""); // Extract the part of the command after "remind me that"
            resultPart = commandPart; // This variable holds the command after the replacement
            userInfo.setRemindMe(command.toLowerCase().contains("remind")); // Set the reminder flag in the model based on the presence of "remind"
            System.out.println("here in result part " + resultPart); // Print the result part for verification
            LocalDateTime extractedTime = timeDetection.extractDateTime(messageDTO.getMessage());
            Timestamp extractedTimestamp = Timestamp.valueOf(extractedTime);
            userInfo.setDetectedTime(extractedTimestamp); // Set the timestamp in your UserInfoModel
            System.out.println("Extracted Timestamp: " + extractedTimestamp);
            userInfo.setResponse(resultPart); // Set the resulting response from processing
        }


        // Extract keywords and POS-tagged words from the command
        Map<String, List<String>> posTaggedWords = extractAndScoreKeywords(commandPart); // Method to get POS-tagged words

        // Calculate the total score from all keywords
        int totalScore = calculateTotalScore(posTaggedWords); // Method to calculate total score

        // Create a UserInfoModel object and populate it with processed data

        userInfo.setUserId(userId); // Set the user ID in the model
        userInfo.setCommandText(command); // Set the full command text

        // Convert each POS list to a single sentence
        userInfo.setNouns(String.join(" ", posTaggedWords.getOrDefault("NOUN", Collections.emptyList()))); // Nouns sentence
        userInfo.setVerbs(String.join(" ", posTaggedWords.getOrDefault("VERB", Collections.emptyList()))); // Verbs sentence
        userInfo.setAdjectives(String.join(" ", posTaggedWords.getOrDefault("ADJ", Collections.emptyList()))); // Adjectives sentence
        userInfo.setAdverbs(String.join(" ", posTaggedWords.getOrDefault("ADV", Collections.emptyList()))); // Adverbs sentence
        userInfo.setPrepositions(String.join(" ", posTaggedWords.getOrDefault("ADP", Collections.emptyList()))); // Prepositions sentence
        userInfo.setPronouns(String.join(" ", posTaggedWords.getOrDefault("PRON", Collections.emptyList()))); // Pronouns sentence
        userInfo.setConjunctions(String.join(" ", posTaggedWords.getOrDefault("CONJ", Collections.emptyList()))); // Conjunctions sentence
        userInfo.setDeterminers(String.join(" ", posTaggedWords.getOrDefault("DET", Collections.emptyList()))); // Determiners sentence
        userInfo.setInterjections(String.join(" ", posTaggedWords.getOrDefault("INTJ", Collections.emptyList()))); // Interjections sentence
        userInfo.setTotalScore(totalScore); // Set the total score

        // Save the UserInfoModel instance to the database
        return userInfo;  // Save and return the saved UserInfoModel
    }

    /**
     * Extract and score keywords with POS tagging.
     */
    private Map<String, List<String>> extractAndScoreKeywords(String command) {
        Map<String, List<String>> posTaggedWords = new HashMap<>();

        // Tokenize the command into words/tokens
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(command);

        try {
            POSTaggerME tagger = new POSTaggerME(posModel);
            String[] tags = tagger.tag(tokens);

            for (int i = 0; i < tokens.length; i++) {
                String word = tokens[i];
                String tag = tags[i];

                // Filter and store words by their POS tags
                posTaggedWords.computeIfAbsent(tag, k -> new ArrayList<>()).add(word);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return posTaggedWords;
    }

    /**
     * Calculate the importance score for POS tags.
     */
    private int getWordScore(String word) {
        // Check the POS of the word and return the score
        return switch (word) {
            case "PROPN" -> 8; // Proper Noun
            case "NOUN" -> 10; // Noun
            case "VERB" -> 6; // Verb
            case "ADJ" -> 5; // Adjective
            case "ADV" -> 4; // Adverb
            case "PRON" -> 3; // Pronoun
            case "ADP" -> 2; // Adposition
            case "DET" -> 1; // Determiner
            case "CONJ" -> 1; // Conjunction
            case "INTJ" -> 1; // Interjection
            default -> 0; // If it's not recognized
        };
    }


    /**
     * Calculate the total score for a map of POS-tagged words.
     */
// Calculate total score based on POS tagged words
    public int calculateTotalScore(Map<String, List<String>> posTaggedWords) {
        return posTaggedWords.entrySet().stream() // Stream through each POS entry
                .mapToInt(entry -> {
                    String posTag = entry.getKey(); // Get the POS tag (e.g., "NOUN")
                    List<String> words = entry.getValue(); // Get the list of words for that tag
                    int scorePerMatch = getWordScore(posTag); // Get the score for this POS tag
                    return scorePerMatch * words.size(); // Calculate total score for this tag
                })
                .sum(); // Sum all scores
    }


    // Process command to UserInfoModel without response
    public UserInfoModel processCommandToUserInfoModel(String command) {
        // Create a UserInfoModel instance to store the processed data
        UserInfoModel userInfo = new UserInfoModel();
        userInfo.setCommandText(command); // Store the full command message as is

        // Extract keywords and POS-tagged words from the command
        Map<String, List<String>> posTaggedWords = extractAndScoreKeywords(command);

        // Calculate the total score from all keywords
        int totalScore = posTaggedWords.values().stream()
                .flatMap(List::stream)
                .mapToInt(this::getWordScore)
                .sum();

        // Populate the UserInfoModel with concatenated POS sentences
        userInfo.setNouns(String.join(" ", posTaggedWords.getOrDefault("NOUN", Collections.emptyList())));
        userInfo.setVerbs(String.join(" ", posTaggedWords.getOrDefault("VERB", Collections.emptyList())));
        userInfo.setAdjectives(String.join(" ", posTaggedWords.getOrDefault("ADJ", Collections.emptyList())));
        userInfo.setAdverbs(String.join(" ", posTaggedWords.getOrDefault("ADV", Collections.emptyList())));
        userInfo.setPrepositions(String.join(" ", posTaggedWords.getOrDefault("ADP", Collections.emptyList())));
        userInfo.setPronouns(String.join(" ", posTaggedWords.getOrDefault("PRON", Collections.emptyList())));
        userInfo.setConjunctions(String.join(" ", posTaggedWords.getOrDefault("CONJ", Collections.emptyList())));
        userInfo.setDeterminers(String.join(" ", posTaggedWords.getOrDefault("DET", Collections.emptyList())));
        userInfo.setInterjections(String.join(" ", posTaggedWords.getOrDefault("INTJ", Collections.emptyList())));
        userInfo.setTotalScore(totalScore); // Set the calculated total score
        System.out.println(totalScore);
        return userInfo; // Return the populated UserInfoModel
    }

}
