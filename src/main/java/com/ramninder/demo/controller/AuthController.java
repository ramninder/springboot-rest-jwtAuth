package com.ramninder.demo.controller;

import com.ramninder.demo.model.ERole;
import com.ramninder.demo.model.Role;
import com.ramninder.demo.model.User;
import com.ramninder.demo.payloads.request.LoginRequest;
import com.ramninder.demo.payloads.request.SignupRequest;
import com.ramninder.demo.payloads.response.JwtResponse;
import com.ramninder.demo.payloads.response.MessageResponse;
import com.ramninder.demo.repos.RoleRepository;
import com.ramninder.demo.repos.UserRepository;
import com.ramninder.demo.security.jwt.JwtUtils;
import com.ramninder.demo.security.service.UserDetailsImpl;
import com.ramninder.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserService  userService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;


    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }


    @PostMapping("/generateToken")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@Valid @RequestBody SignupRequest signUpRequest){

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        System.out.println(user.getUsername());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Please assign some role to user"));
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "manager":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRole(roles);
        userService.saveUsers(user);
        return   ResponseEntity.ok(new MessageResponse("User Successfully Registered"));
    }

//    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') OR hasRole('DEVELOPER')")
//    public ResponseEntity<Optional<User>> findUserById(@PathVariable Long id){
//            return  new ResponseEntity<Optional<User>>(userService.findUserById(id), HttpStatus.OK);
//    }
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    public ResponseEntity<Optional<User>> updateUserInformation(@RequestBody User user, @PathVariable Long id){
//        return  new ResponseEntity<Optional<User>>(userService.updateUserInfo(user, id), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<String> userDeletedById(@PathVariable Long id){
//         userService.deleteUser(id);
//        return  ResponseEntity.ok("User is deleted");
//    }
}
