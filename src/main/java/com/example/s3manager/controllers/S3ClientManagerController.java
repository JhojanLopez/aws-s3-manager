package com.example.s3manager.controllers;

import com.example.s3manager.models.BucketDto;
import com.example.s3manager.models.CopyObjectDto;
import com.example.s3manager.services.S3ClientManagerService;
import com.example.s3manager.utils.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@RequestMapping("/s3-client")
@RequiredArgsConstructor
@RestController
public class S3ClientManagerController {
    private final S3ClientManagerService s3ClientManagerService;

    @PostMapping("/bucket")
    public ResponseEntity<?> createBucket(@RequestBody BucketDto bucketDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(s3ClientManagerService.createBucket(bucketDto.getName()));
    }
    @DeleteMapping("/bucket")
    public ResponseEntity<?> deleteBucket(@RequestParam String bucketName){
        return ResponseEntity.ok(s3ClientManagerService.deleteBucket(bucketName));
    }
    @GetMapping("/bucket")
    public ResponseEntity<?> getAllBucket(){
        return ResponseEntity.ok(s3ClientManagerService.getAllBucket());
    }

    @PostMapping("/object")
    public ResponseEntity<?> saveObject(@RequestParam("file") MultipartFile file, @RequestParam String bucketName,
                                        @RequestParam String prefix,
                                        @RequestParam(required = false) Optional<String> renameFileRequest)
            throws IOException {
        String renameFile = null;
        if (renameFileRequest.isPresent()){
            renameFile = renameFileRequest.get();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(s3ClientManagerService
                .saveObject(bucketName, prefix, file, renameFile));
    }

    @DeleteMapping("/object")
    public ResponseEntity<?> deleteObject(@RequestParam String bucketName, @RequestParam String key){
        return ResponseEntity.ok(s3ClientManagerService.deleteObject(bucketName, key));
    }

    @GetMapping("/object")
    public ResponseEntity<?> downloadObject(@RequestParam String bucketName, @RequestParam String key)
            throws IOException {
        byte[] bytes = s3ClientManagerService.downloadObject(bucketName, key);

        String fileName = S3Utils.getFileNameFromKey(key);
        String headerValue = "attachment; filename=\"" + fileName + "\"";
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(bytes);
    }

    @GetMapping("/object/pre-signed-link")
    public ResponseEntity<?> generatePreSignedLink(@RequestParam String bucketName, @RequestParam String key,
                                                   @RequestParam long expirationMinutes) {
        return ResponseEntity.ok(s3ClientManagerService.generatePreSignedLink(bucketName, key, expirationMinutes));
    }

    @PostMapping("/object/copy")
    public ResponseEntity<?> copyObject(@RequestBody CopyObjectDto copyObjectDto) {
        return ResponseEntity.ok(s3ClientManagerService.copyObject(copyObjectDto));
    }
}
