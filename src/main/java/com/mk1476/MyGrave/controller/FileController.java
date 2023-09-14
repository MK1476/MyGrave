package com.mk1476.MyGrave.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.mk1476.MyGrave.service.S3Service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/files")
public class FileController {

    private final S3Service s3Service;

    @Autowired
    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Use the S3Service method to upload the file to your S3 bucket
            s3Service.uploadFile("mygrave-app-storage", "destination-key", file.getInputStream(), createMetadataForFile(file));
            
            // Handle a successful upload and return an appropriate response
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            // Handle errors and return an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file.");
        }
    }

    @GetMapping("/{key}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String key) {
        try {
            // Use the S3Service method to download the file from your S3 bucket
            S3Object s3Object = s3Service.downloadFile("mygrave-app-storage", key);
            
            // Create a response with the downloaded file's InputStream
            S3ObjectInputStream fileInputStream = s3Object.getObjectContent();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()))
                    .body(new InputStreamResource(fileInputStream));
        } catch (Exception e) {
            // Handle errors and return an appropriate response
           String errorMessage = "File not found."; // Modify this error message as needed
           ByteArrayInputStream errorInputStream = new ByteArrayInputStream(errorMessage.getBytes());
           InputStreamResource errorResource = new InputStreamResource(errorInputStream);

           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_PLAIN)
                .body(errorResource);
        }
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteFile(@PathVariable String key) {
        try {
            // Use the S3Service method to delete the file from your S3 bucket
            s3Service.deleteFile("mygrave-app-storage", key);
            
            // Handle a successful deletion and return an appropriate response
            return ResponseEntity.ok("File deleted successfully.");
        } catch (Exception e) {
            // Handle errors and return an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the file.");
        }
    }

    // Helper method to create metadata for the uploaded file
    private ObjectMetadata createMetadataForFile(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return metadata;
    }
}
