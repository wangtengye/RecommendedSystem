package com.se.mapper;


import com.se.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Jack on 2017/7/1.
 */
public interface UserRepository extends JpaRepository<User,Integer>{
    User findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
}
