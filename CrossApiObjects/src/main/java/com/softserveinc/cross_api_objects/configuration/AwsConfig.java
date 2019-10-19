package com.softserveinc.cross_api_objects.configuration;

import java.util.List;

public class AwsConfig {

    private String accessKey;
    private String secretKey;

    private String bucketName;

    private List<String> allowedFileTypes;

    private List<String> allowedImageTypes;

    private Long maxFileSize;

    private Long maxImageSize;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public List<String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(List<String> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public List<String> getAllowedImageTypes() {
        return allowedImageTypes;
    }

    public void setAllowedImageTypes(List<String> allowedImageTypes) {
        this.allowedImageTypes = allowedImageTypes;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Long getMaxImageSize() {
        return maxImageSize;
    }

    public void setMaxImageSize(Long maxImageSize) {
        this.maxImageSize = maxImageSize;
    }
}
