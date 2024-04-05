package com.example.s3manager.controllers;

import com.example.s3manager.models.BucketDto;
import com.example.s3manager.services.S3TemplateManagerService;
import com.example.s3manager.utils.S3Utils;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequestMapping("/s3-template")
@RequiredArgsConstructor
@RestController
public class S3TemplateManagerController {
    private final S3TemplateManagerService s3TemplateManagerService;

    @PostMapping("/bucket")
    public ResponseEntity<?> createBucket(@RequestBody BucketDto bucketDto){
        return ResponseEntity.ok(s3TemplateManagerService.createBucket(bucketDto.getName()));
    }

    @DeleteMapping("/bucket")
    public ResponseEntity<?> deleteBucket(@RequestParam String bucketName){
        return ResponseEntity.ok(s3TemplateManagerService.deleteBucket(bucketName));
    }

    @PostMapping("/object")
    public ResponseEntity<?> saveObject(@RequestParam("file") MultipartFile file, @RequestParam String bucketName,
                                        @RequestParam String prefix, @RequestParam(required = false) Optional<String> renameFile)
            throws IOException {
        return ResponseEntity.ok(s3TemplateManagerService.saveObject(bucketName, prefix, file, renameFile));
    }

    @DeleteMapping("/object")
    public ResponseEntity<?> deleteObject(@RequestParam String s3Url){
        return ResponseEntity.ok(s3TemplateManagerService.deleteObject(s3Url));
    }

    @GetMapping("/object")
    public ResponseEntity<?> downloadObject(@RequestParam String bucketName, @RequestParam String prefix)
            throws IOException {
        S3Resource s3Resource = s3TemplateManagerService.downloadObject(bucketName, prefix);
        if (!s3Resource.exists()){
            return ResponseEntity.notFound().build();
        }
        String fileName = S3Utils.getFileNameFromS3Resource(s3Resource);
        byte[] bytes = s3Resource.getInputStream().readAllBytes();
        String headerValue = "attachment; filename=\"" + fileName + "\"";
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(bytes);
    }

    @GetMapping("/object/pre-signed-link")
    public ResponseEntity<?> generatePreSignedLink(@RequestParam String bucketName, @RequestParam String key,
                                                   @RequestParam int expirationMinutes)
            throws IOException {
        return ResponseEntity.ok(s3TemplateManagerService.generatePreSignedLink(bucketName, key, expirationMinutes));
    }
}
