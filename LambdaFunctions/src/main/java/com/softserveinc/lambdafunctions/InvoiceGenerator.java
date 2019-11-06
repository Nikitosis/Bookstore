package com.softserveinc.lambdafunctions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.Base64;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.softserveinc.cross_api_objects.models.User;
import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InvoiceGenerator implements RequestHandler<TestClass,JSONObject> {
    @Override
    public JSONObject handleRequest(TestClass user, Context context) {

        Document document=new Document();
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk("Hello World", font);
        try {
            document.add(chunk);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.close();
        byte[] encoded=Base64.encode(out.toByteArray());
        String body=new String(encoded);

        JSONObject headerJson = new JSONObject();
        headerJson.put("x-custom-header", "my custom header value");

        JSONObject responseBody = new JSONObject();
        responseBody.put("bytes", body);

        JSONObject response=new JSONObject();
        response.put("isBase64Encoded",false);
        response.put("statusCode", 200);
        response.put("headers", headerJson);
        response.put("body", responseBody.toString());

        return response;
    }
}
