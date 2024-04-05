package com.example.s3manager.services;

import com.example.s3manager.models.CopyObjectDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Interface to multiple uses cases for S3Client, for the pre signed link it used s3Operations
 * */
public interface S3ClientManagerService {
    String createBucket(String bucketName);
    String deleteBucket(String bucketName);
    List<String> getAllBucket();
    String saveObject(String bucketName, String prefix, MultipartFile file, String renameFile) throws IOException;
    byte[] downloadObject(String bucketName, String prefix) throws IOException;
    String deleteObject(String bucketName, String key);
    String generatePreSignedLink(String bucketName, String key, long expirationMinutes);
    String copyObject(CopyObjectDto copyObjectDto);
}
