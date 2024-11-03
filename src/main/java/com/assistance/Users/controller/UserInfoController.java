package com.assistance.Users.controller;

import com.assistance.Users.dto.MessageDTO;
import com.assistance.Users.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;


    @PostMapping("/api")
    public MessageDTO reply(@RequestBody MessageDTO messageDTO) {

        // Split the message into words and check for exact matches of "set" and "as"
        String[] words = messageDTO.getMessage().split("\\s+"); // Split message by whitespace into an array of words
        List<String> wordList = Arrays.asList(words); // Convert array to list for easier matching

        // Check if both "set" and "as" exist as individual words in the message
        if (wordList.contains("set") && wordList.contains("as")) {
            return getSample(messageDTO);
        } else if (wordList.contains("remind") && wordList.contains("me")){
            return getSample(messageDTO);
        }
        else if(wordList.contains("all") && wordList.contains("memory")){
            return userInfoService.getAllData(messageDTO);
        }
        else if(wordList.contains("next") && wordList.contains("work")){
            return userInfoService.getNextWork(messageDTO);
        }

        return userInfoService.getReply(messageDTO); // Return the regular response if conditions are not met
    }

    @PostMapping("/pro")
    public MessageDTO getSample(@RequestBody MessageDTO messageDTO) {
        try{
            return userInfoService.NlpProcessor(messageDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }






//
//
//    @PostMapping("/saveByVoice")
//    public MessageDTO saveByVoice(@RequestBody MessageDTO messageDTO){
//
//        return userInfoService.saveByVoice(messageDTO);
//    }




}
