package com.assistance.Users.service;

import com.assistance.Users.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRegService userRegService;

//    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    public boolean verifyPassword(String email , String password){

        UserModel userModel = userRegService.findByEmail(email);

        return userModel !=null && password.equals(userModel.getPassword());
    }

}
