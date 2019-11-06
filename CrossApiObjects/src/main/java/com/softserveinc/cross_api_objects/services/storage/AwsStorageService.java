package com.softserveinc.cross_api_objects.services.storage;

import com.softserveinc.cross_api_objects.configuration.AwsS3Config;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class AwsStorageService {

    private AwsS3Config awsS3Config;

    private AmazonS3 client;

    public AwsStorageService(AwsS3Config awsS3Config, AmazonS3 client) {
        this.awsS3Config = awsS3Config;
        this.client = client;
    }

    //check if file types are correct
    //returns path
    public String uploadFile(StoredFile file, CannedAccessControlList access) throws IOException {
        String fileName=file.getFileName();
        String type=fileName.substring(fileName.lastIndexOf(".")+1);

        String resultFileName= UUID.randomUUID().toString()+"."+type;

        client.putObject(
                new PutObjectRequest(awsS3Config.getBucketName(), resultFileName, file.getInputStream(), new ObjectMetadata())
                        .withCannedAcl(access)
        );

        return resultFileName;
    }

    public String getFileUrl(String path){
        return client.getUrl(awsS3Config.getBucketName(),path).toString();
    }

    public InputStream getFileInputStream(String path){
        return client.getObject(awsS3Config.getBucketName(),path)
                .getObjectContent();
    }

    public boolean isAllowedFileType(String fileName){
        String fileType=fileName.substring(fileName.lastIndexOf(".")+1);
        return awsS3Config.getAllowedFileTypes().contains(fileType);
    }

    public boolean isAllowedImageType(String imageName){
        String imageType=imageName.substring(imageName.lastIndexOf(".")+1);
        return awsS3Config.getAllowedImageTypes().contains(imageType);
    }

}
