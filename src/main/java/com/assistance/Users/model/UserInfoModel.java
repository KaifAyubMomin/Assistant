package com.assistance.Users.model;

import jakarta.persistence.*;

import java.sql.Timestamp; // Import Timestamp for detected_time and created_at
import java.time.LocalDateTime;

@Entity // Marks this class as a JPA entity
@Table(name = "user_info") // Specifies the name of the table in the database
public class UserInfoModel{

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies that the ID should be auto-incremented
    private Integer commandId; // Primary key for the command

    @Column(name = "user_id", nullable = false) // Maps this field to the user_id column and sets it as not nullable
    private Integer userId; // Foreign key for user_registration table

    @Column(name = "command_text", nullable = false) // Maps to command_text column and sets it as not nullable
    private String commandText; // Full command text

    @Column(name = "nouns") // Maps to nouns column
    private String nouns; // String representation of nouns

    @Column(name = "verbs") // Maps to verbs column
    private String verbs; // String representation of verbs

    @Column(name = "adjectives") // Maps to adjectives column
    private String adjectives; // String representation of adjectives

    @Column(name = "adverbs") // Maps to adverbs column
    private String adverbs; // String representation of adverbs

    @Column(name = "prepositions") // Maps to prepositions column
    private String prepositions; // String representation of prepositions

    @Column(name = "pronouns") // Maps to pronouns column
    private String pronouns; // String representation of pronouns

    @Column(name = "conjunctions") // Maps to conjunctions column
    private String conjunctions; // String representation of conjunctions

    @Column(name = "determiners") // Maps to determiners column
    private String determiners; // String representation of determiners

    @Column(name = "interjections") // Maps to interjections column
    private String interjections; // String representation of interjections

    @Column(name = "total_score") // Maps to total_score column
    private Integer totalScore; // Total score based on word scores

    @Column(name = "response") // Maps to response column
    private String response; // Assistant response

    @Column(name = "detected_time") // Maps to detected_time column
    private Timestamp detectedTime; // Detected timestamp for reminders

    @Column(name = "remind_me", columnDefinition = "BOOLEAN DEFAULT FALSE") // Maps to remind_me column
    private Boolean remindMe; // Boolean column for reminder

    @Column(name = "created_at", updatable = false) // Prevent updates to this field
    private LocalDateTime createdAt; // Use LocalDateTime or Date as per your requirement

    public UserInfoModel() {
    }

    public UserInfoModel(Integer userId ,String commandText, String nouns, String verbs, String adjectives,
                         String adverbs, String prepositions, String pronouns, String conjunctions,
                         String determiners, String interjections, Integer totalScore,
                         String response,  Boolean remindMe ,Timestamp detectedTime) {
        this.commandText = commandText;
        this.nouns = nouns;
        this.verbs = verbs;
        this.adjectives = adjectives;
        this.adverbs = adverbs;
        this.prepositions = prepositions;
        this.pronouns = pronouns;
        this.conjunctions = conjunctions;
        this.determiners = determiners;
        this.interjections = interjections;
        this.totalScore = totalScore;
        this.response = response;
        this.detectedTime = detectedTime;
        this.remindMe = remindMe;
        this.userId = userId;
    }

//    @Override
//    public String toString() {
//        return "UserInfoModel{" +
//
//                ", userId=" + userId +
//                ", commandText='" + commandText + '\'' +
//                ", nouns='" + nouns + '\'' +
//                ", verbs='" + verbs + '\'' +
//                ", adjectives='" + adjectives + '\'' +
//                ", adverbs='" + adverbs + '\'' +
//                ", prepositions='" + prepositions + '\'' +
//                ", pronouns='" + pronouns + '\'' +
//                ", conjunctions='" + conjunctions + '\'' +
//                ", determiners='" + determiners + '\'' +
//                ", interjections='" + interjections + '\'' +
//                ", totalScore=" + totalScore +
//                ", response='" + response + '\'' +
//                ", detectedTime=" + detectedTime +
//                ", remindMe=" + remindMe +
//                ", createdAt=" + createdAt +
//                '}';
//    }

    // Getters and Setters for each field

    public Integer getCommandId() {
        return commandId; // Returns the command ID
    }

    public void setCommandId(Integer commandId) {
        this.commandId = commandId; // Sets the command ID
    }

    public Integer getUserId() {
        return userId; // Returns the user ID
    }

    public void setUserId(Integer userId) {
        this.userId = userId; // Sets the user ID
    }

    public String getCommandText() {
        return commandText; // Returns the command text
    }

    public void setCommandText(String commandText) {
        this.commandText = commandText; // Sets the command text
    }

    public String getNouns() {
        return nouns; // Returns the nouns
    }

    public void setNouns(String nouns) {
        this.nouns = nouns; // Sets the nouns
    }

    public String getVerbs() {
        return verbs; // Returns the verbs
    }

    public void setVerbs(String verbs) {
        this.verbs = verbs; // Sets the verbs
    }

    public String getAdjectives() {
        return adjectives; // Returns the adjectives
    }

    public void setAdjectives(String adjectives) {
        this.adjectives = adjectives; // Sets the adjectives
    }

    public String getAdverbs() {
        return adverbs; // Returns the adverbs
    }

    public void setAdverbs(String adverbs) {
        this.adverbs = adverbs; // Sets the adverbs
    }

    public String getPrepositions() {
        return prepositions; // Returns the prepositions
    }

    public void setPrepositions(String prepositions) {
        this.prepositions = prepositions; // Sets the prepositions
    }

    public String getPronouns() {
        return pronouns; // Returns the pronouns
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns; // Sets the pronouns
    }

    public String getConjunctions() {
        return conjunctions; // Returns the conjunctions
    }

    public void setConjunctions(String conjunctions) {
        this.conjunctions = conjunctions; // Sets the conjunctions
    }

    public String getDeterminers() {
        return determiners; // Returns the determiners
    }

    public void setDeterminers(String determiners) {
        this.determiners = determiners; // Sets the determiners
    }

    public String getInterjections() {
        return interjections; // Returns the interjections
    }

    public void setInterjections(String interjections) {
        this.interjections = interjections; // Sets the interjections
    }

    public Integer getTotalScore() {
        return totalScore; // Returns the total score
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore; // Sets the total score
    }

    public String getResponse() {
        return response; // Returns the response
    }

    public void setResponse(String response) {
        this.response = response; // Sets the response
    }

    public Timestamp getDetectedTime() {
        return detectedTime; // Returns the detected time
    }

    public void setDetectedTime(Timestamp time) {
        this.detectedTime = time; // Sets the detected time
    }

    public Boolean getRemindMe() {
        return remindMe; // Returns the remind me flag
    }

    public void setRemindMe(Boolean remindMe) {
        this.remindMe = remindMe; // Sets the remind me flag
    }

    public LocalDateTime getCreatedAt() {
        return createdAt; // Returns the creation timestamp
    }

    // No setter for createdAt to prevent it from being changed after creation

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // Set createdAt to current timestamp before saving
    }
}
