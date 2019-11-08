package com.softserveinc.invoicer.services;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.softserveinc.cross_api_objects.services.storage.AwsStorageService;
import com.softserveinc.cross_api_objects.services.storage.StoredFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class InvoiceAwsSaverService {
    private AwsStorageService awsStorageService;

    @Autowired
    public InvoiceAwsSaverService(AwsStorageService awsStorageService) {
        this.awsStorageService = awsStorageService;
    }

    public String saveInvoice(StoredFile storedFile){
        String fileUrl=null;
        try {
            String filePath=awsStorageService.uploadFile(storedFile,CannedAccessControlList.PublicRead);
            fileUrl=awsStorageService.getFileUrl(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileUrl;
    }
}
