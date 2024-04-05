package com.example.s3manager.services;

import io.awspring.cloud.s3.S3Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
/**
 * Interface to multiple uses cases for S3Template
 * */
public interface S3TemplateManagerService {
    String createBucket(String bucketName);
    String deleteBucket(String bucketName);
    String saveObject(String bucketName, String prefix, MultipartFile file, Optional<String> renameFile) throws IOException;
    S3Resource downloadObject(String bucketName, String prefix) throws IOException;
    String deleteObject(String s3Url);
    String generatePreSignedLink(String bucketName, String prefix, int expirationMinutes);
}
