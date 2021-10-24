package ir.mohsen.sazvar.myapplication.radar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;


import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreatePdf extends AppCompatActivity {
    private static final String LOG_TAG = "GeneratePDF";

    private EditText preparedBy;
    private File pdfFile;
    private String filename = "Sample2.pdf";
    private String filepath = "MyInvoices";

    private BaseFont bfBold;
Button btnPdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
btnPdf=(Button)findViewById(R.id.btnpdf);
btnPdf.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        generatePDF("mohsen");
    }
});
        //get reference to the edittext so pull data out
     //   preparedBy = (EditText) findViewById(R.id.preparedBy);
        //need to load license from the raw resources for iText
        //skip this if you are going to use droidText
      //  InputStream license = this.getResources().openRawResource(R.raw.itextkey);
       // LicenseKey.loadLicenseFile(license);

        //check if external storage is available so that we can dump our PDF file there
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.v(LOG_TAG, "External Storage not available or you don't have permission to write");
        }
        else {
            //path for the PDF file in the external storage
            pdfFile = new File(getExternalFilesDir(filepath), filename);
        }

    }

    public void printPDF(View v) {



            //start the process of creating the PDF and then print it

                generatePDF("customer");


    }

    private void generatePDF(String personName){

        //create a new document
        Document document = new Document();

        try {

            PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
          //
            document.addAuthor("Skholingua");
            document.addCreationDate();
            document.addProducer();
            document.addCreator("www.skholingua.com");
            document.addTitle("Skholingua");
            document.setPageSize(PageSize.A4);
// left,right,top,bottom
            document.setMargins(36, 36, 36, 36);
            document.setMarginMirroring(true);

docWriter.setRunDirection(PdfWriter.DirectionR2L);

            /* Create Paragraph and Set Font */
            Paragraph p1 = new Paragraph("Skholingua Tutorial\nLearn how to create a PDF in android with image and dynamic text form User.");

            /* Create Set Font and its Size */
            Font paraFont= new Font(Font.FontFamily.HELVETICA);
            paraFont.setSize(16);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);

//add paragraph to document
            document.add(p1);

          //  Anchor anchor = new Anchor("First Chapter", catFont);
           // anchor.setName("First Chapter");

// Second parameter is the number of the chapter
        //    Chapter catPart = new Chapter(new Paragraph(anchor), 1);





//
            PdfContentByte cb = docWriter.getDirectContent();
            //initialize fonts for text printing
            initializeFonts();

            //the company logo is stored in the assets which is read only
            //get the logo and print on the document
            InputStream inputStream = getAssets().open("a1.png");
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image companyLogo = Image.getInstance(stream.toByteArray());
            companyLogo.setAbsolutePosition(25,700);
            companyLogo.scalePercent(25);
            document.add(companyLogo);

            //creating a sample invoice with some customer data
            createHeadings(cb,400,780,"Company Name");
            createHeadings(cb,400,765,"Address Line 1");
            createHeadings(cb,400,750,"Address Line 2");
            createHeadings(cb,400,735,"City, State - ZipCode");
            createHeadings(cb,400,720,"Country");

            //list all the products sold to the customer
            float[] columnWidths = {1.5f, 2f, 5f, 2f,2f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setTotalWidth(500f);

            PdfPCell cell = new PdfPCell(new Phrase("Qty"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Item Number"));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Item Description"));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Price"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Ext Price"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            table.setHeaderRows(1);

            DecimalFormat df = new DecimalFormat("0.00");
            for(int i=0; i < 15; i++ ){
                double price = Double.valueOf(df.format(Math.random() * 10));
                double extPrice = price * (i+1) ;
                table.addCell(String.valueOf(i+1));
                table.addCell("ITEM" + String.valueOf(i+1));
                table.addCell("Product Description - SIZE " + String.valueOf(i+1));
                table.addCell(df.format(price));
                table.addCell(df.format(extPrice));
            }

            //absolute location to print the PDF table from
            table.writeSelectedRows(0, -1, document.leftMargin(), 650, docWriter.getDirectContent());

            //print the signature image along with the persons name
            inputStream = getAssets().open("signature.png");
            bmp = BitmapFactory.decodeStream(inputStream);
            stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image signature = Image.getInstance(stream.toByteArray());
            signature.setAbsolutePosition(400f, 150f);
            signature.scalePercent(25f);
            document.add(signature);

            createHeadings(cb,450,135,personName);

            document.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }

        //PDF file is now ready to be sent to the bluetooth printer using PrintShare
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setPackage("com.dynamixsoftware.printershare");
        i.setDataAndType(Uri.fromFile(pdfFile),"application/pdf");
//        startActivity(i);

        Toast.makeText(CreatePdf.this, ""+pdfFile.getPath(), Toast.LENGTH_SHORT).show();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.fromFile(pdfFile);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share file using"));



    }

    private void createHeadings(PdfContentByte cb, float x, float y, String text){

        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }


    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private void initializeFonts(){


        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
















}