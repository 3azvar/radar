package ir.mohsen.sazvar.myapplication.radar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Main22Activity extends AppCompatActivity {
    public static final String ARABIC = "\u200F";
    Font f;
    private static final String TAG = "PdfCreatorActivity";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    ImageView imgdownload;
    ArrayList<GiftitemPOJO> MyList1;
    GiftitemPOJO giftitemPOJO;
    Context context;
    GiftitemPOJO name;
    GiftitemPOJO amount_big;
    GiftitemPOJO unit_big;
    GiftitemPOJO num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imgdownload = findViewById(R.id.downloadpdf);
        context = this;
        f = FontFactory.getFont("assets/font/IRANSansMobile.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        giftitemPOJO = new GiftitemPOJO();
        DoLOgin doLOgin = new DoLOgin();
        doLOgin.execute();
        imgdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdfWrapper();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createPdfWrapper() throws FileNotFoundException, DocumentException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            createPdf();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        String pdfname = "Sazvar2.pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);

        //   Document document = new Document(PageSize.A4, 36, 36, 120, 36);
        Document document = new Document(PageSize.A4, 15, 15, 90, 15);

        Font ff2 = FontFactory.getFont("assets/font/IRANSansMobile_Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);


        PdfPTable table001 = new PdfPTable(new float[]{16});
        //  table001.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table001.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        // table001.setWidthPercentage(50);
        table001.getDefaultCell().setFixedHeight(30);
        table001.setSpacingAfter(20);
        table001.setTotalWidth(PageSize.A4.getWidth());


        Phrase ph000 = new Phrase("");
        ph000.add(new Chunk(ARABIC, ff2));
        ph000.add(new Chunk("فاکتور فروش"));
        PdfPCell cellx = new PdfPCell(ph000);
        cellx.setBorder(PdfPCell.NO_BORDER);
        cellx.setFixedHeight(30);
        cellx.setHorizontalAlignment(Element.ALIGN_CENTER);
        table001.addCell(cellx);

        ///
        PdfPTable table = new PdfPTable(new float[]{2.5f, 2.5f, 4, 1});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(25);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);


        Phrase ph1 = new Phrase("");
        ph1.add(new Chunk(ARABIC, f));
        ph1.add(new Chunk("ردیف"));
        table.addCell(ph1);
        Phrase ph2 = new Phrase("");
        ph2.add(new Chunk(ARABIC, f));
        ph2.add(new Chunk("شرح"));
        table.addCell(ph2);


        Phrase ph3 = new Phrase("");
        ph3.add(new Chunk(ARABIC, f));
        ph3.add(new Chunk("مبلغ"));
        table.addCell(ph3);
        Phrase ph4 = new Phrase("");
        ph4.add(new Chunk(ARABIC, f));
        ph4.add(new Chunk("تاریخ"));
        table.addCell(ph4);


        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.LIGHT_GRAY);

        }

        for (int i = 0; i < MyList1.size(); i++) {
            num = MyList1.get(i);
            name = MyList1.get(i);
            amount_big = MyList1.get(i);
            unit_big = MyList1.get(i);

            String numn = num.getItem_num();
            String namen = name.getItem_name();
            String amountn_big = amount_big.getItem_amount_big();
            String unitn_big = unit_big.getItem_unit_big();


            Phrase ph_name = new Phrase("");
            ph_name.add(new Chunk(ARABIC, f));
            ph_name.add(new Chunk(namen));

            Phrase ph_unit = new Phrase("");
            ph_unit.add(new Chunk(ARABIC, f));
            ph_unit.add(new Chunk(unitn_big));



            table.addCell(String.valueOf(numn));
            table.addCell(ph_name);
            table.addCell(String.valueOf(amountn_big));
            table.addCell(ph_unit);


        }

        PdfWriter docWriter = PdfWriter.getInstance(document, output);





        document.open();
        PdfContentByte cb = docWriter.getDirectContent();



        document.addAuthor("Sazvar");
        document.addCreationDate();
        document.addProducer();
        document.addCreator("www.sazvar.com");
        document.addTitle("3azvar");
        document.setPageSize(PageSize.A4);
        document.setMarginMirroring(true);

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("a1.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image companyLogo = null;
        try {
            companyLogo = Image.getInstance(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        companyLogo.setAbsolutePosition(25, 750);
        companyLogo.scalePercent(25);

        document.add(table001);
        document.add(table);


        PdfPTable table1 = new PdfPTable(new float[]{4, 3, 3, 1, 5});
        table1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table1.getDefaultCell().setFixedHeight(50);
        table1.setTotalWidth(PageSize.A4.getWidth());
        table1.setWidthPercentage(100);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);


        Phrase ph34 = new Phrase("");
        ph34.add(new Chunk(ARABIC, f));
        ph34.add(new Chunk("جزئیات : "));

        PdfPCell cell11 = new PdfPCell(ph34);
        cell11.setBorder(PdfPCell.RIGHT | PdfPCell.TOP | PdfPCell.BOTTOM);
        cell11.setFixedHeight(50);
        cell11.setPadding(7);
        table1.addCell(cell11);

        cell11 = new PdfPCell(new Phrase(""));
        cell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell11.setBorder(PdfPCell.TOP | PdfPCell.BOTTOM);
        cell11.setFixedHeight(50);
        table1.addCell(cell11);

        cell11 = new PdfPCell(new Phrase(""));
        cell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell11.setBorder(PdfPCell.TOP | PdfPCell.BOTTOM);
        cell11.setFixedHeight(50);
        table1.addCell(cell11);

        cell11 = new PdfPCell(new Phrase(" "));
        cell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell11.setBorder(PdfPCell.TOP | PdfPCell.BOTTOM);
        cell11.setFixedHeight(50);
        table1.addCell(cell11);


        Phrase ph43 = new Phrase("");
        ph43.add(new Chunk(ARABIC, f));
        ph43.add(new Chunk("مجموع : "));
        cell11 = new PdfPCell(ph43);
        cell11.setPadding(10);
        cell11.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell11.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP | PdfPCell.BOTTOM);
        cell11.setFixedHeight(50);
        table1.addCell(cell11);


        document.add(table1);


        PdfPTable table2 = new PdfPTable(new float[]{1, 1});
        table2.setHorizontalAlignment(Element.ALIGN_LEFT);
        table2.setWidthPercentage(25);
        table.getDefaultCell().setFixedHeight(50);
        table2.setSpacingAfter(20);
        PdfPCell cellw = new PdfPCell(new Phrase(""));
        cellw.setBorder(PdfPCell.LEFT | PdfPCell.TOP | PdfPCell.BOTTOM);
        cellw.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cellw.setFixedHeight(50);
        table2.addCell(cellw);
        cellw = new PdfPCell(new Phrase(""));
        cellw.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellw.setBorder(PdfPCell.RIGHT | PdfPCell.TOP | PdfPCell.BOTTOM);
        cellw.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cellw.setFixedHeight(50);
        table2.addCell(cellw);
        document.add(table2);


        try {
            inputStream = getAssets().open("a1.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        bmp = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        Image signature = null;
        try {
            signature = Image.getInstance(stream2.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        signature.setAbsolutePosition(400f, 150f);
        signature.scalePercent(80f);
        document.add(signature);

        createHeadings(cb, 450, 135, "Mohsen Sazvar");

        document.close();
        previewPdf();
    }

    private void previewPdf() {

        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "برنامه ای برای باز کردن فایل pdf وجود ندارد!", Toast.LENGTH_SHORT).show();
        }
    }

    class DoLOgin extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {

                MyList1 = new ArrayList<GiftitemPOJO>();

                for (int i = 0; i < 5; i++) {
                    giftitemPOJO.setItem_num((i + 1) + "");
                    giftitemPOJO.setItem_name("تاید دستی دریا ( عددی 40)");
                    giftitemPOJO.setItem_amount_big("5000000");
                    giftitemPOJO.setItem_unit_big("1398/6/23");

                    MyList1.add(giftitemPOJO);

                    giftitemPOJO = new GiftitemPOJO();
                }

            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);


        }
    }

    private BaseFont bfBold;

    private void createHeadings(PdfContentByte cb, float x, float y, String text) {
        cb.beginText();
        try {
            bfBold = BaseFont.createFont("assets/font/XBZar.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cb.setFontAndSize(bfBold, 20);
        cb.setTextMatrix(x, y);
        cb.showText(text.trim());
        cb.endText();

    }

}