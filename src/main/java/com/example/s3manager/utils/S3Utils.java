package com.example.s3manager.utils;

import io.awspring.cloud.s3.S3Resource;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class S3Utils {
    /**
     * it has next structure: folder-a/folder-b..etc (it dont have / initial or final)
     * */
    public static String validatePrefixOrKey(String path){
        if (path.startsWith("/")){
            path = path.replaceFirst("/","");
        }
        if (path.endsWith("/")){
            return path.substring(0, path.length()-1);
        }
        return path;
    }

    public static String getFileNameFromS3Resource(S3Resource s3Resource){
        String[] split = Objects.requireNonNull(s3Resource.getFilename()).split("/");
        return split[split.length-1];
    }

    public static String getFileNameFromKey(String key) {
        String validatePrefixKey = S3Utils.validatePrefixOrKey(key);
        String[] split = validatePrefixKey.split("/");
        return split[split.length-1];
    }
}
