package com.mk1476.MyGrave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.mk1476.MyGrave.Database.User;
import com.mk1476.MyGrave.Database.UserRepository;
import com.mk1476.MyGrave.exception.UserAlreadyExistsException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private S3UserService s3UserService; 

    public User registerUser(String spaceName, String password) {
        // Check if the user already exists
        if (userRepository.findBySpaceName(spaceName) != null) {
            throw new UserAlreadyExistsException("User already exists.");
        }
        
        // Hash the user's password and save the user
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setSpaceName(spaceName);
        user.setAuthenticationKey(hashedPassword);
        s3UserService.addUser(user);
        return userRepository.save(user);
    }

    public boolean authenticateUser(String spaceName, String providedKey) {

       

        User user = userRepository.findBySpaceName(spaceName);

        if (user != null) {
            // Compare the provided key with the stored hashed key
            String storedKey = user.getAuthenticationKey();
            return BCrypt.checkpw(providedKey, storedKey);
        }

        return false; // User not found or key doesn't match
    }
}
