package com.assistance.Users.repository;


import com.assistance.Users.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegRepository extends JpaRepository<UserModel, Integer> {

    UserModel findByEmail(String email);

}
