package com.assistance.Users.service;

import com.assistance.Users.dto.MessageDTO;
import com.assistance.Users.model.UserInfoModel;
import com.assistance.Users.model.UserModel;
import com.assistance.Users.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserRegService userRegService;


    // Method to get a reply message based on user input
    public MessageDTO getReply(MessageDTO messageDTO) {
        Optional<UserModel> userModel = userRegService.getUserById(messageDTO.getId());
        if (userModel.isEmpty()) {
            return new MessageDTO(messageDTO.getId(), "Sorry but Your not registered as User ."); // Return null if user not found
        }

        String username = userModel.get().getUsername();
        int requestId = userModel.get().getUserId();
        String requestMessage = messageDTO.getMessage();

        if (requestMessage == null || requestMessage.isEmpty()) {
            return null; // Return null if message is null or empty
        }

        // Process the request message to extract UserInfoModel
        CommandProcessorNlp commandProcessorNlp = new CommandProcessorNlp();
        UserInfoModel extractedUserInfo = commandProcessorNlp.processCommandToUserInfoModel(requestMessage);
        int totalExpectedScore = calculateTotalScore(extractedUserInfo);
        List<UserInfoModel> allResponses = userInfoRepository.findAllByUserId(requestId);
        int matchCount = 0;
        int Accuracy = 0;
        String bestResponse1 = "";
        String bestResponse2 = "";
        int highestMatchCount = 0;
        boolean perMatch = false;

        // Loop through each response and calculate match score
        for (UserInfoModel singleResponse : allResponses) {
            matchCount = 0;

            matchCount += calculateMatchWithScore(singleResponse.getNouns(), extractedUserInfo.getNouns(), "NOUN");
            matchCount += calculateMatchWithScore(singleResponse.getVerbs(), extractedUserInfo.getVerbs(), "VERB");
            matchCount += calculateMatchWithScore(singleResponse.getAdjectives(), extractedUserInfo.getAdjectives(), "ADJ");
            matchCount += calculateMatchWithScore(singleResponse.getAdverbs(), extractedUserInfo.getAdverbs(), "ADV");
            matchCount += calculateMatchWithScore(singleResponse.getPrepositions(), extractedUserInfo.getPrepositions(), "ADP");
            matchCount += calculateMatchWithScore(singleResponse.getPronouns(), extractedUserInfo.getPronouns(), "PRON");
            matchCount += calculateMatchWithScore(singleResponse.getConjunctions(), extractedUserInfo.getConjunctions(), "CONJ");
            matchCount += calculateMatchWithScore(singleResponse.getDeterminers(), extractedUserInfo.getDeterminers(), "DET");
            matchCount += calculateMatchWithScore(singleResponse.getInterjections(), extractedUserInfo.getInterjections(), "INTJ");

//            if (singleResponse.getResponse() != null && singleResponse.getResponse().equalsIgnoreCase(requestMessage)) {
//                matchCount += 5; // Extra score for exact response match
//            }

            if (matchCount == totalExpectedScore) {
                System.out.println("100 match found ");
                highestMatchCount = matchCount;
                bestResponse1 = bestResponse1 + (perMatch ? " and " : " ") + singleResponse.getResponse();
                perMatch = true;
            }

            if (matchCount > highestMatchCount) {
                System.out.println("side is taken .....");
                highestMatchCount = matchCount;
                bestResponse2 = singleResponse.getResponse();
            }

            Accuracy = (matchCount * 100) / totalExpectedScore;
            System.out.println("Accuracy  : " + getAccuracyBar(Accuracy) + matchCount + (!bestResponse1.isEmpty() ? bestResponse1: bestResponse2));

        }

        Accuracy = (highestMatchCount * 100) / totalExpectedScore;
        System.out.println("Highest Accuracy  : " + getAccuracyBar(Accuracy) + matchCount +  (!bestResponse1.isEmpty() ? bestResponse1: bestResponse2));

        if (Accuracy >= 80) {
            return new MessageDTO(requestId, (!bestResponse1.isEmpty() ? bestResponse1: bestResponse2));
        } else if (Accuracy > 70) {
            return new MessageDTO(requestId,  (!bestResponse1.isEmpty() ? bestResponse1: bestResponse2) + " is it correct " + username );
        } else {
            return new MessageDTO(requestId, "Sorry " + username + " but i am unable to understand your question properly ; please can you repeat the question completely and clearly");
        }
    }

    private int calculateMatchWithScore(String dbKeywords, String extractedKeywords, String posTag) {
        if (dbKeywords == null || extractedKeywords == null) return 0;

        // Convert sentence into a list of words
        List<String> dbKeywordsList = Arrays.asList(dbKeywords.split(" "));
        List<String> extractedKeywordsList = Arrays.asList(extractedKeywords.split(" "));

        int scorePerMatch = getWordScore(posTag); // Get the score for the POS type
        int count = 0;

        // Increment count for each matching word based on POS score
        for (String word : extractedKeywordsList) {
            if (dbKeywordsList.contains(word)) {
                count += scorePerMatch;
            }
        }
        return count;
    }


    // Helper method to calculate total score for extracted data
    private int calculateTotalScore(UserInfoModel userInfo) {
        int totalScore = 0;

        totalScore += userInfo.getNouns() != null ? userInfo.getNouns().split(" ").length * getWordScore("NOUN") : 0;
        totalScore += userInfo.getVerbs() != null ? userInfo.getVerbs().split(" ").length * getWordScore("VERB") : 0;
        totalScore += userInfo.getAdjectives() != null ? userInfo.getAdjectives().split(" ").length * getWordScore("ADJ") : 0;
        totalScore += userInfo.getAdverbs() != null ? userInfo.getAdverbs().split(" ").length * getWordScore("ADV") : 0;
        totalScore += userInfo.getPrepositions() != null ? userInfo.getPrepositions().split(" ").length * getWordScore("ADP") : 0;
        totalScore += userInfo.getPronouns() != null ? userInfo.getPronouns().split(" ").length * getWordScore("PRON") : 0;
        totalScore += userInfo.getConjunctions() != null ? userInfo.getConjunctions().split(" ").length * getWordScore("CONJ") : 0;
        totalScore += userInfo.getDeterminers() != null ? userInfo.getDeterminers().split(" ").length* getWordScore("DET") : 0;
        totalScore += userInfo.getInterjections() != null ? userInfo.getInterjections().split(" ").length * getWordScore("INTJ") : 0;

        return totalScore;
    }

    // Score mapping for each POS tag
    private int getWordScore(String tag) {
        return switch (tag) {
            case "PROPN" -> 8;
            case "NOUN" -> 10;
            case "VERB" -> 6;
            case "ADJ" -> 5;
            case "ADV" -> 4;
            case "PRON" -> 3;
            case "ADP" -> 2;
            case "DET" -> 1;
            case "CONJ" -> 1;
            case "INTJ" -> 1;
            default -> 0;
        };
    }

    // Process commands when no matching response is found
    private String processCommand(String message, String username) {
        Map<String, String> commandResponses = new HashMap<>();
        commandResponses.put("prime minister", "It seems you need help, " + username + ". What can I assist you with?");
        commandResponses.put("status", "Your current status is: Everything is functioning normally!");
        commandResponses.put("info", "Here is the information you requested...");

        for (Map.Entry<String, String> entry : commandResponses.entrySet()) {
            if (message.toLowerCase().contains(entry.getKey())) {
                return entry.getValue(); // Return matched response
            }
        }

        return null;
    }

    // Method to convert accuracy percentage into visual bar
    private String getAccuracyBar(int accuracy) {
        int totalBars = 5;
        int filledBars = (int) Math.round(totalBars * (accuracy / 100.0));

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filledBars; i++) {
            bar.append("🟩");
        }
        for (int i = filledBars; i < totalBars; i++) {
            bar.append("⬜");
        }
        return bar.toString() + " (" + accuracy + "%)";
    }



    private TimeDetection timeDetection = new TimeDetection();


    public MessageDTO NlpProcessor(MessageDTO messageDTO) {
        try {
            Optional<UserModel> user = userRegService.getUserById(messageDTO.getId());

            if (user.isPresent()) {
                CommandProcessorNlp commandProcessorNlp = new CommandProcessorNlp();
                UserInfoModel processedInfo = commandProcessorNlp.processCommand(messageDTO);
                System.out.println("processed data: " + processedInfo.getDetectedTime());
                userInfoRepository.save(processedInfo);

                return new MessageDTO(
                        messageDTO.getId(),
                        "Processed command successfully: "
                );
            } else {
                return new MessageDTO(messageDTO.getId(), "You are not registered as a user.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + " due to: " + e.getCause());
            return new MessageDTO(messageDTO.getId(), "Currently unable to process the request due to a network issue.");
        }
    }





    // Saves UserInfoModel to the repository
    public void saveUserInfo(UserInfoModel userInfoModel) {
        userInfoRepository.save(userInfoModel);
    }

    public MessageDTO getAllData(MessageDTO messageDTO) {

        Optional<UserModel> userModel = userRegService.getUserById(messageDTO.getId());
        if (userModel.isEmpty()) {
            return null;
        }


        List<UserInfoModel> sample = userInfoRepository.findAllByUserIdAndRemindMeTrueAndDetectedTimeAfter(messageDTO.getId() , LocalDateTime.now());
      if(sample.isEmpty()){
          return new MessageDTO(messageDTO.getId(), "Currently you don't have any data");
      }
        sample.sort(Comparator.comparing(UserInfoModel::getDetectedTime)); // Sort by detected time ascending

        StringBuilder responses = new StringBuilder();

        // Loop through each UserInfoModel in the list
        sample.forEach(one -> {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("' on 'd MMMM yyyy 'at' h:mm a ");
            String formattedTime = one.getDetectedTime().toLocalDateTime().format(dateTimeFormatter);

            // Append each response to the StringBuilder with a space
            responses.append(one.getResponse());
            responses.append(formattedTime).append("    And   ");

        });
        // Create and return a new MessageDTO with the id and concatenated responses
        System.out.println("Response is here : " + responses.toString());
        return new MessageDTO(messageDTO.getId(), responses.toString().trim()); // Trim to remove any trailing space

    }


    public MessageDTO getNextWork(MessageDTO messageDTO){

        Optional<UserModel> userModel = userRegService.getUserById(messageDTO.getId());
        if (userModel.isEmpty()) {
            return new MessageDTO( messageDTO.getId() , "Sorry but Your not registered as User ."); // Return null if user not found
        }

        Optional<UserInfoModel> getSample = userInfoRepository.findFirstByUserIdAndRemindMeTrueAndDetectedTimeAfterOrderByDetectedTimeAsc( messageDTO.getId() , LocalDateTime.now());

        if(getSample.isEmpty()){
            return new MessageDTO(messageDTO.getId(), "Currently you don't have any data");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("' on 'd MMMM yyyy 'at' h:mm a ");
        String formattedTime = getSample.get().getDetectedTime().toLocalDateTime().format(dateTimeFormatter);

        System.out.println("Detection : " + getSample.get().getResponse() + formattedTime);
        return new MessageDTO(messageDTO.getId() , getSample.get().getResponse() + formattedTime);
    }


}
