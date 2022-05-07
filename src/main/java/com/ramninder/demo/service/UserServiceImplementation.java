package com.ramninder.demo.service;

import com.ramninder.demo.model.ERole;
import com.ramninder.demo.model.Role;
import com.ramninder.demo.model.User;
import com.ramninder.demo.payloads.request.SignupRequest;
import com.ramninder.demo.payloads.response.MessageResponse;
import com.ramninder.demo.repos.RoleRepository;
import com.ramninder.demo.repos.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImplementation implements UserService{

    private UserRepository userRepo;
    private PasswordEncoder encoder;

    private RoleRepository roleRepo;

    @Autowired
    public UserServiceImplementation( UserRepository userRepo, PasswordEncoder encoder, RoleRepository roleRepo){
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.roleRepo = roleRepo;
    }



        @Override
        public User saveUsers(User user) {
            return userRepo.save(user);

    }

//    @Override
//    public Optional<User> findUserById(Long id) {
//        return  userRepo.findById(id);
//    }
//
//    @Override
//    public Optional<User> updateUserInfo(User updateUser, Long id) {
//        return userRepo.findById(id).map(user -> {
//            user.setUsername(updateUser.getUsername());
//            user.setEmail(updateUser.getEmail());
//            user.setPassword(updateUser.getPassword());
//            return userRepo.save(user);
//        });
//    }
//
//    @Override
//    public void deleteUser(Long id) {
//         userRepo.deleteById(id);
//    }
}
