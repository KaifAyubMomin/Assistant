package com.assistance.Users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// http://localhost:8083/register       -post (username, email, password, phoneNumber, String.valueOf(timestamp), preferences)
// http://localhost:8083/login          -post(email , password)
// http://localhost:8083/getAllUsers    -get()

@SpringBootApplication
public class UsersRegApplication {

	public static void main(String[] args) {

		SpringApplication.run(UsersRegApplication.class, args);

		System.out.println("hello world ...");
	}
}
