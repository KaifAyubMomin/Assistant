package com.assistance.Users.service; // Package declaration

import opennlp.tools.postag.POSModel; // Import POSModel for POS tagging
import opennlp.tools.postag.POSTaggerME; // Import POSTaggerME for tagging sentences
import java.io.InputStream; // Import InputStream for file handling
import java.io.IOException; // Import IOException for handling input/output errors

public class OpenNLPExample {

    public static void main(String[] args) {
        // Load the POS tagging model
        try (InputStream modelIn = OpenNLPExample.class.getResourceAsStream("/opennlp-en-ud-ewt-pos-1.1-2.4.0.bin")) { // Load model file from resources
            if (modelIn == null) {
                System.err.println("Error: POS model file not found in resources folder.");
                return; // Exit if the model cannot be found
            }

            // Initialize the POS model and the tagger
            POSModel posModel = new POSModel(modelIn); // Create a POSModel from the model input stream
            POSTaggerME posTagger = new POSTaggerME(posModel); // Initialize the POS tagger with the model

            // Example input sentence for POS tagging
            String[] sentence = new String[]{"The", "cat", "sat", "on", "the", "mat."}; // Sample sentence
            String[] tags = posTagger.tag(sentence); // Tag the sentence using the POS tagger

            // Print each word with its corresponding POS tag
            for (int i = 0; i < sentence.length; i++) {
                System.out.println(sentence[i] + " -> " + tags[i]); // Print word and tag
            }
        } catch (IOException e) {
            System.err.println("Failed to load the model."); // Error loading the model
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
}
