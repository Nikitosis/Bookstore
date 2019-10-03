package com.services.storage;

import com.MainConfig;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
public class AwsStorageService {

    private MainConfig mainConfig;

    private AmazonS3 client;

    @Autowired
    public AwsStorageService(MainConfig mainConfig){
        this.mainConfig=mainConfig;

        AWSCredentials credentials=new BasicAWSCredentials(
                mainConfig.getAwsConfig().getAccessKey(),
                mainConfig.getAwsConfig().getSecretKey()
        );

        client= AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    //returns path
    public String uploadFile(StoredFile file, CannedAccessControlList access) throws IOException {
        String fileName=file.getContentDisposition().getFileName();
        String type=fileName.substring(fileName.lastIndexOf(".")+1);

        String resultFileName= UUID.randomUUID().toString()+"."+type;

        client.putObject(
                new PutObjectRequest(mainConfig.getAwsConfig().getBucketName(), resultFileName, file.getInputStream(), new ObjectMetadata())
                        .withCannedAcl(access)
        );

        return resultFileName;
    }

    public URL getFileUrl(String path){
        return client.getUrl(mainConfig.getAwsConfig().getBucketName(),path);
    }

    public InputStream getFileInputStream(String path){
        return client.getObject(mainConfig.getAwsConfig().getBucketName(),path)
                .getObjectContent();
    }

}
