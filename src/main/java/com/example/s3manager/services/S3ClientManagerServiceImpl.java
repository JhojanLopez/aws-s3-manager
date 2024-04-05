package com.example.s3manager.services;

import com.example.s3manager.models.CopyObjectDto;
import com.example.s3manager.utils.S3Utils;
import io.awspring.cloud.s3.S3Operations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3ClientManagerServiceImpl implements S3ClientManagerService {

    private final S3Client s3Client;
    private final S3Operations s3Operations;
    @Override
    public String createBucket(String bucketName) {
        CreateBucketResponse bucket = s3Client.createBucket(CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build());
        return bucket.location();
    }

    @Override
    public String deleteBucket(String bucketName) {
        DeleteBucketResponse bucketResponse = s3Client.deleteBucket(DeleteBucketRequest.builder()
                        .bucket(bucketName)
                        .build());
        return bucketResponse.sdkHttpResponse().toString();
    }

    @Override
    public List<String> getAllBucket() {
        return s3Client.listBuckets().buckets()
                .stream()
                .map(Bucket::name)
                .toList();

    }

    @Override
    public String saveObject(String bucketName, String prefix, MultipartFile file, String renameFile) throws IOException {
        String validatedPrefix = S3Utils.validatePrefixOrKey(prefix);

        if (renameFile == null){
            validatedPrefix = validatedPrefix + "/" + file.getOriginalFilename();
        }else {
            validatedPrefix = validatedPrefix + "/" + renameFile;
        }

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(validatedPrefix)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));
        return validatedPrefix;
    }

    @Override
    public byte[] downloadObject(String bucketName, String key) throws IOException {
        //if it doesn't exist the object it throws NoSuchKeyException
        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
        return object.readAllBytes();
    }

    @Override
    public String deleteObject(String bucketName, String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
        return bucketName + key;
    }

    @Override
    public String generatePreSignedLink(String bucketName, String key, long expirationMinutes) {
        URL signedGetURL = s3Operations.createSignedGetURL(bucketName, key, Duration.ofMinutes(expirationMinutes));
        return signedGetURL.toString();
    }

    @Override
    public String copyObject(CopyObjectDto copyObjectDto) {
        String currentKeyValidated = S3Utils.validatePrefixOrKey(copyObjectDto.getCurrentKey());
        String targetPrefixValidated = S3Utils.validatePrefixOrKey(copyObjectDto.getTargetPrefix());
        String fileName = S3Utils.getFileNameFromKey(currentKeyValidated);
        
        if (copyObjectDto.getRenameFile() == null){
            targetPrefixValidated = targetPrefixValidated + "/" + fileName;
        }else {
            targetPrefixValidated = targetPrefixValidated + "/" + copyObjectDto.getRenameFile();
        }
        String targetKey = targetPrefixValidated;

        s3Client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(copyObjectDto.getCurrentBucketName())
                .sourceKey(currentKeyValidated)
                .destinationBucket(copyObjectDto.getTargetBucketName())
                .destinationKey(targetKey)
                .build());

        return targetKey;
    }
}
