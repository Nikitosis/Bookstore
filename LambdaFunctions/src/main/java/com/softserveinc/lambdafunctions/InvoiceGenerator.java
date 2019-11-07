package com.softserveinc.lambdafunctions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.softserveinc.cross_api_objects.models.InvoiceData;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class InvoiceGenerator implements RequestHandler<InvoiceData,JSONObject> {
    public static final String BOLD = "OpenSans-Bold.ttf";

    @Override
    public JSONObject handleRequest(InvoiceData invoiceData, Context context) {

        ByteArrayOutputStream out= null;
        try {
            out = createInvoiceBytes(invoiceData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //encode bytes
        byte[] encoded= Base64.getEncoder().encode(out.toByteArray());
        String body=new String(encoded);

        JSONObject headerJson = new JSONObject();
        headerJson.put("x-custom-header", "my custom header value");

        JSONObject response=new JSONObject();
        response.put("isBase64Encoded",false);
        response.put("statusCode", 200);
        response.put("headers", headerJson);
        response.put("body", body);

        return response;
    }

    private ByteArrayOutputStream createInvoiceBytes(InvoiceData invoiceData) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont bold=createFont(BOLD);

        document.setFont(bold).setFontSize(12);

        //add header
        document.add(
                new Paragraph()
                    .setTextAlignment(TextAlignment.CENTER)
                    .add(new Text("Bookstore invoice").setFont(bold).setFontSize(16).setFontColor(Color.WHITE))
                    .setBackgroundColor(Color.BLACK)
        );

        //invoice general information
        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMultipliedLeading(1)
                        .add(new Text(String.format("%s %s\n", invoiceData.getUserName(), invoiceData.getUserSurname()))
                                .setFont(bold).setFontSize(14))
                        .add(invoiceData.getDateTime().toString())
                        //.add(invoiceData.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        );

        //invoice content
        document.add(getLineItemTable(invoiceData,bold));

        document.close();

        return out;
    }

    private PdfFont createFont(String name) throws IOException {
        byte[] fontContents = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(name));
        FontProgram fontProgram = FontProgramFactory.createFont(fontContents);
        return PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H);
    }

    private Table getLineItemTable(InvoiceData invoiceData,PdfFont bold) {
        Table table = new Table(
                new UnitValue[]{
                        new UnitValue(UnitValue.PERCENT, 50f),
                        new UnitValue(UnitValue.PERCENT, 50f)})
                .setWidthPercent(100)
                .setMarginTop(10).setMarginBottom(10);
        table.addHeaderCell(createCell("Item:", bold));
        table.addHeaderCell(createCell("Price:", bold));

        table.addCell(createCell(invoiceData.getBookName()));
        table.addCell(createCell(invoiceData.getPayment()));

        return table;
    }

    private Cell createCell(String text) {
        return new Cell().setPadding(0.8f)
                .add(new Paragraph(text)
                        .setMultipliedLeading(1));
    }
    private Cell createCell(String text, PdfFont font) {
        return new Cell().setPadding(0.8f)
                .add(new Paragraph(text)
                        .setFont(font).setMultipliedLeading(1));
    }
}
