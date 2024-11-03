package com.assistance.Users.service;

import com.assistance.Users.model.ResponseOnLoginModel;
import com.assistance.Users.model.UserModel;
import com.assistance.Users.repository.UserRegRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserRegService {

    @Autowired
    private UserRegRepository userRegRepository;

//    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    public String registerUser(String username , String email , String password , String phoneNumber , Timestamp createdAt , String preferences){
        if (emailExists(email)) {
          return "Email already registered";
        }

        // Create a new user model and save it
        UserModel newUser = new UserModel(username, email, password, phoneNumber, createdAt, preferences);
        userRegRepository.save(newUser);
        return "User registered successfully";
    }

    // Method to check if the email already exists in the database
    private boolean emailExists(String email) {
        Optional<UserModel> existingUser = Optional.ofNullable(findByEmail(email)); // Assuming this method is defined in your UserRepository
        return existingUser.isPresent(); // Return true if the user exists
    }


    public UserModel findByEmail(String email) {
        return userRegRepository.findByEmail(email);
    }

    // response for registration
    public Optional<ResponseOnLoginModel> getUserByEmailWithMessageForReg(String email){
        UserModel userModel = findByEmail(email);
        return Optional.of(new ResponseOnLoginModel("User Registered Successfully ....", userModel.getUserId(),
                userModel.getUsername(), userModel.getEmail(), userModel.getPhoneNumber(), userModel.getCreatedAt(), userModel.getPreferences()));
    }

    // response for login
    public Optional<ResponseOnLoginModel> getUserByEmailWithMessage(String email){
        UserModel userModel = findByEmail(email);
        return Optional.of(new ResponseOnLoginModel("Login Successfull..", userModel.getUserId(),
                userModel.getUsername(), userModel.getEmail(), userModel.getPhoneNumber(), userModel.getCreatedAt(), userModel.getPreferences()));
    }

    public List<UserModel> getAllUsers(){
        return userRegRepository.findAll();
    }

    public Optional<UserModel> getUserById(int id){
        return userRegRepository.findById(id);
    }

    public Boolean isUser(int id){
        return userRegRepository.existsById(id);
    }
}
