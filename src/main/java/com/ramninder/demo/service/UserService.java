package com.ramninder.demo.service;

import com.ramninder.demo.model.User;
import com.ramninder.demo.payloads.request.SignupRequest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    public User saveUsers(User user);
   // public Optional<User> findUserById(Long id);

   // public Optional<User> updateUserInfo(User updateUser, Long id);

   // public void deleteUser(Long id);
}
