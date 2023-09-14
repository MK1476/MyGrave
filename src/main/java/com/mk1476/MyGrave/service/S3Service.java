package com.mk1476.MyGrave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import java.io.InputStream;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Autowired
    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    // Method to upload a file to S3
    public void uploadFile(String bucketName, String key, InputStream fileInputStream, ObjectMetadata metadata) {
        amazonS3.putObject(bucketName, key, fileInputStream, metadata);
    }

    // Method to download a file from S3
    public S3Object downloadFile(String bucketName, String key) {
        return amazonS3.getObject(bucketName, key);
    }

    // Method to delete a file from S3
    public void deleteFile(String bucketName, String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    // Add error handling and security measures as needed
}

