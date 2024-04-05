package com.example.s3manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CopyObjectDto {
    private String currentBucketName;
    private String targetBucketName;
    private String currentKey;
    private String targetPrefix;
    private String renameFile;
}
