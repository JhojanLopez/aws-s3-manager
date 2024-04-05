package com.example.s3manager.services;

import com.example.s3manager.utils.S3Utils;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class S3TemplateManagerServiceImpl implements S3TemplateManagerService {
    private final S3Template s3Template;

    @Override
    public String createBucket(String bucketName) {
        return s3Template.createBucket(bucketName);
    }

    @Override
    public String deleteBucket(String bucketName) {
        s3Template.deleteBucket(bucketName);
        return bucketName;
    }

    @Override
    public String saveObject(String bucketName,String prefix, MultipartFile file, Optional<String> renameFile)
            throws IOException {
        String validatedPrefix = S3Utils.validatePrefixOrKey(prefix);

        if (renameFile.isPresent()){
            validatedPrefix = validatedPrefix + "/" + renameFile.get();
        }else {
            validatedPrefix = validatedPrefix + "/" + file.getOriginalFilename();
        }

        S3Resource upload = s3Template.upload(bucketName, validatedPrefix, file.getInputStream());
        return upload.getURL().getPath();
    }

    @Override
    public S3Resource downloadObject(String bucketName, String prefix) throws IOException {
        String validatedPrefix = S3Utils.validatePrefixOrKey(prefix);
        return s3Template.download(bucketName, validatedPrefix);
    }

    @Override
    public String deleteObject(String s3Url) {
        s3Url = S3Utils.validatePrefixOrKey(s3Url);
        s3Template.deleteObject(s3Url);
        return s3Url;
    }

    @Override
        public String generatePreSignedLink(String bucketName, String prefix, int expirationMinutes) {
        String validatedPrefix = S3Utils.validatePrefixOrKey(prefix);
        URL signedGetURL = s3Template.createSignedGetURL(bucketName, validatedPrefix,
                Duration.ofMinutes(expirationMinutes));
        return signedGetURL.toString();
    }
}
