package com.assistance.Users.repository;

import com.assistance.Users.model.UserInfoModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoModel, Integer> {

    List<UserInfoModel> findAllByUserId(int userId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_info " +
            "(user_id, command_text, nouns, verbs, adjectives, adverbs, prepositions, pronouns, conjunctions, determiners, interjections, total_score, response, detected_time, remind_me) " +
            "VALUES (:userId, :commandText, :nouns, :verbs, :adjectives, :adverbs, :prepositions, :pronouns, :conjunctions, :determiners, :interjections, :totalScore, :response, NOW(), :remindMe)",
            nativeQuery = true)
    void insertUserInfo(@Param("userId") int userId, @Param("commandText") String commandText,
                        @Param("nouns") String nouns, @Param("verbs") String verbs, @Param("adjectives") String adjectives,
                        @Param("adverbs") String adverbs, @Param("prepositions") String prepositions,
                        @Param("pronouns") String pronouns, @Param("conjunctions") String conjunctions,
                        @Param("determiners") String determiners, @Param("interjections") String interjections,
                        @Param("totalScore") int totalScore, @Param("response") String response,
                        @Param("remindMe") boolean remindMe);

   Optional<UserInfoModel> findFirstByUserIdAndRemindMeTrueAndDetectedTimeAfterOrderByDetectedTimeAsc(int userId , LocalDateTime currrentTime );

    List<UserInfoModel> findAllByUserIdAndRemindMeTrueAndDetectedTimeAfter(int userId, LocalDateTime currentDateTime);
}
