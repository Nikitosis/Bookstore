package com.softserveinc.feecharger.services;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.Base64;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.services.storage.AwsStorageService;
import com.softserveinc.cross_api_objects.services.storage.StoredFile;
import com.softserveinc.feecharger.MainConfig;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@Service
public class InvoiceService {
    private MainConfig mainConfig;
    private AwsStorageService awsStorageService;

    @Autowired
    public InvoiceService(MainConfig mainConfig, AwsStorageService awsStorageService) {
        this.mainConfig = mainConfig;
        this.awsStorageService = awsStorageService;
    }

    /**
    * @return url of the file
     */
    public String createInvoiceFile(User user, Book book, BigDecimal price){
        Client client = ClientBuilder.newClient();

        StoredFile storedFile=new StoredFile();
        storedFile.setFileName("Invoice.pdf");
        storedFile.setInputStream(new ByteArrayInputStream(getPdfBytes()));

        String fileUrl=null;
        try {
            String filePath=awsStorageService.uploadFile(storedFile,CannedAccessControlList.PublicRead);
            fileUrl=awsStorageService.getFileUrl(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileUrl;
    }

    private byte[] getPdfBytes(){
        Client client = ClientBuilder.newClient();

        String response=client.target("https://qerhbbqfd2.execute-api.us-east-2.amazonaws.com/default/InvoicePDFGenerator")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        JSONObject jsonObject=(JSONObject)JSONValue.parse(response);

        byte[] decoded=Base64.decode(jsonObject.get("bytes").toString().getBytes());

        return decoded;
    }
}
