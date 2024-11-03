package com.assistance.Users.controller; // Package declaration for controller

import com.assistance.Users.model.ResponseOnLoginModel;
import com.assistance.Users.model.UserModel; // Importing the UserModel class
import com.assistance.Users.service.AuthenticationService; // Importing the AuthenticationService class
import com.assistance.Users.service.UserRegService; // Importing the UserRegService class
import org.springframework.beans.factory.annotation.Autowired; // Importing the Autowired annotation
import org.springframework.http.HttpStatus; // Importing HttpStatus for response codes
import org.springframework.http.ResponseEntity; // Importing ResponseEntity for HTTP responses
import org.springframework.web.bind.annotation.*; // Importing annotations for handling web requests

import java.sql.Timestamp; // Importing Timestamp for date/time representation
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List; // Importing List for handling collections
import java.util.Optional;


// http://localhost:8083/auth/register       -post (username, email, password, phoneNumber, String.valueOf(timestamp), preferences)
// http://localhost:8083/auth/login          -post(email , password)
// http://localhost:8083/auth/getAllUsers    -get()


@RestController // Annotation to indicate this class is a REST controller
public class UsersRegController {

    @Autowired // Automatically injects the UserRegService bean
    private UserRegService userRegService; // Service for user registration
    @Autowired // Automatically injects the AuthenticationService bean
    private AuthenticationService authenticationService; // Service for authentication

    @PostMapping("/register") // Mapping POST requests to /register
    public ResponseEntity<?> registerUser(@RequestParam String username, // Parameter for username
                                               @RequestParam String email, // Parameter for email
                                               @RequestParam String password, // Parameter for password
                                               @RequestParam String phoneNumber, // Parameter for phone number
                                               @RequestParam String createdAt, // Parameter for creation timestamp
                                               @RequestParam String preferences) { // Parameter for user preferences
        try {
            // Specify the expected date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            dateFormat.setLenient(false);  // Enforce strict matching

            // Parse the String into a java.util.Date, then convert to Timestamp
            java.util.Date parsedDate = dateFormat.parse(createdAt);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());

            // Call the service to handle registration and check the return message
            String registrationMessage = userRegService.registerUser(username, email, password, phoneNumber, timestamp, preferences);

            // Return the response based on the message from the service
            if (registrationMessage.equals("User registered successfully")) {
                return ResponseEntity.status(HttpStatus.CREATED).body(userRegService.getUserByEmailWithMessageForReg(email));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseOnLoginModel(registrationMessage , null, null,null ,null ,null ,null));
            }

        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseOnLoginModel("Invalid timestamp format. Use format: 'yyyy-MM-dd HH:mm:ss.SSS'"  ,null,null,null,null,null,null));
        }
    }

    @PostMapping("/login") // Mapping POST requests to /login
    public ResponseEntity<?> userLogin(@RequestParam String email, // Parameter for email
                                            @RequestParam String password) { // Parameter for password
        // Verifies the user's password using the authentication service
        if (authenticationService.verifyPassword(email, password)) {
            // Returns success response if credentials are valid

            return ResponseEntity.ok(userRegService.getUserByEmailWithMessage(email));
        } else {
            // Returns unauthorized response if credentials are invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseOnLoginModel("Invalid Credentials.." ,0,null,null,null,null,null));
        }
    }

    @GetMapping("/getAllUsers") // Mapping GET requests to /getAllUsers
    public List<UserModel> getAllUsers() {
        // Retrieves and returns a list of all users from the UserRegService
        return userRegService.getAllUsers();
    }

    @GetMapping("/getUser/{userId}")
    public Optional<UserModel> getUser(@PathVariable int userId){
        return userRegService.getUserById(userId);
    }



    @PostMapping("/api/isuser")
    public Boolean isUser(@RequestBody int id){
        return userRegService.isUser(id);
    }
}
