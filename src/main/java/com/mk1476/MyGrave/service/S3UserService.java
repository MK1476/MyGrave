package com.mk1476.MyGrave.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.mk1476.MyGrave.Database.User;

import org.springframework.stereotype.Service;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class S3UserService {

    private final AmazonS3 amazonS3;
    private final String bucketName = "mygrave-app-storage";
    private final String fileName = "users.json";

    public S3UserService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void addUser(User user) {
        try {
            // Retrieve existing user data from S3
            List<User> existingUsers = getUsersFromS3();

            // Add the new user to the list
            existingUsers.add(user);

            // Convert the updated list to JSON
            String updatedJson = convertUserListToJson(existingUsers);

            // Upload the updated JSON back to S3
            uploadJsonToS3(updatedJson);
        } catch (Exception e) {
            // Handle errors
        }
    }

    public Optional<User> getUserByUsername(String username) {
        try {
            // Retrieve existing user data from S3
            List<User> existingUsers = getUsersFromS3();

            // Find the user by username
            return existingUsers.stream()
                .filter(user -> user.getSpaceName().equals(username))
                .findFirst();
        } catch (Exception e) {
            // Handle errors
            return Optional.empty();
        }
    }

    private List<User> getUsersFromS3() {
        try {
            // Download the JSON file from S3
            S3Object s3Object = amazonS3.getObject(bucketName, fileName);
            InputStream inputStream = s3Object.getObjectContent();
            
            // Read the JSON content and convert it to a list of User objects
            List<User> users = convertJsonToUserList(inputStream);
            
            // Close the input stream
            inputStream.close();
            
            return users;
        } catch (Exception e) {
            // Handle errors or return an empty list if the file doesn't exist yet
            return new ArrayList<>();
        }
    }

    private void uploadJsonToS3(String json) throws IOException {
        // Create a new S3 object with the updated JSON content
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(json.length());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());

        // Upload the JSON object to S3
        amazonS3.putObject(bucketName, fileName, inputStream, metadata);
    }

    private List<User> convertJsonToUserList(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, new TypeReference<List<User>>() {});
    }
    
    private String convertUserListToJson(List<User> users) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(users);
    }
}

