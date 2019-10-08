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
                mainConfig.getAwsS3Config().getAccessKey(),
                mainConfig.getAwsS3Config().getSecretKey()
        );

        client= AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    //check if file types are correct
    //returns path
    public String uploadFile(StoredFile file, CannedAccessControlList access) throws IOException {
        String fileName=file.getFileName();
        String type=fileName.substring(fileName.lastIndexOf(".")+1);

        String resultFileName= UUID.randomUUID().toString()+"."+type;

        client.putObject(
                new PutObjectRequest(mainConfig.getAwsS3Config().getBucketName(), resultFileName, file.getInputStream(), new ObjectMetadata())
                        .withCannedAcl(access)
        );

        return resultFileName;
    }

    public URL getFileUrl(String path){
        return client.getUrl(mainConfig.getAwsS3Config().getBucketName(),path);
    }

    public InputStream getFileInputStream(String path){
        return client.getObject(mainConfig.getAwsS3Config().getBucketName(),path)
                .getObjectContent();
    }

    public boolean isAllowedFileType(String fileName){
        String fileType=fileName.substring(fileName.lastIndexOf(".")+1);
        return mainConfig.getAwsS3Config().getAllowedFileTypes().contains(fileType);
    }

    public boolean isAllowedImageType(String imageName){
        String imageType=imageName.substring(imageName.lastIndexOf(".")+1);
        return mainConfig.getAwsS3Config().getAllowedImageTypes().contains(imageType);
    }

}
