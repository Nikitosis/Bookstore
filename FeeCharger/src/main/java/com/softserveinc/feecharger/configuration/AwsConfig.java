package com.softserveinc.feecharger.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.softserveinc.cross_api_objects.services.storage.AwsStorageService;
import com.softserveinc.feecharger.MainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {
    @Autowired
    private MainConfig mainConfig;

    @Bean
    public AmazonS3 createAwsClient(){
        AWSCredentials credentials=new BasicAWSCredentials(
                mainConfig.getAwsS3Config().getAccessKey(),
                mainConfig.getAwsS3Config().getSecretKey()
        );

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    @Bean
    public AwsStorageService getAwsStorageService(){
        return new AwsStorageService(mainConfig.getAwsS3Config(),createAwsClient());
    }
}
