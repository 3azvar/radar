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

public class Main2Activity extends AppCompatActivity {
    public static final String FONT = "resources/fonts/NotoNaskhArabic-Regular.ttf";
    public static final String ARABIC2 = "\u0627\u0644\u0633\u0639\u0631 \u0627\u0644\u0627\u062c\u0645\u0627\u0644\u064a";
    public static final String ARABIC = "\u200F";
    Font f;
    Font ff;


    private static final String TAG = "PdfCreatorActivity";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    ImageView imgdownload;
    // ConnectionClass connectionClass;
    ArrayList<GiftitemPOJO> MyList1;
    GiftitemPOJO giftitemPOJO;
    Context context;
    GiftitemPOJO name;
    GiftitemPOJO amount_low;
    GiftitemPOJO unit_low;
    GiftitemPOJO amount_big;
    GiftitemPOJO unit_big;
    GiftitemPOJO takhfif;
    GiftitemPOJO fi;
    GiftitemPOJO num;
    GiftitemPOJO total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imgdownload = findViewById(R.id.downloadpdf);
        context = this;
        f = FontFactory.getFont("assets/font/IRANSansMobile(FaNum).ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
       ff = f;

        giftitemPOJO = new GiftitemPOJO();
        //  connectionClass = new ConnectionClass();
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
        Document document = new Document(PageSize.A4, 15, 15, 15, 15);

        ///
        Font ff2 = FontFactory.getFont("assets/font/IRANSansMobile_Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont baseFont = null;
        try {
            baseFont = BaseFont.createFont("assets/font/IRANSansMobile_Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(Main2Activity.this, "" + BaseFont.IDENTITY_H, Toast.LENGTH_LONG).show();

//////////////
        PdfPTable table001 = new PdfPTable(new float[]{16});
        table001.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table001.getDefaultCell().setFixedHeight(40);
        table001.setTotalWidth(PageSize.A4.getWidth());
        table001.setWidthPercentage(100);
        table001.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table001.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        // Font f = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        //  Font f=new Font(bfBold2,15);
        //    Toast.makeText(Main2Activity.this, ""+f.getSize(), Toast.LENGTH_SHORT).show();
        Phrase phx = new Phrase("");
        phx.add(new Chunk(ARABIC, ff2));
        phx.add(new Chunk("فاکتور فروش"));
        //  document.add(ph);


        PdfPCell cell100 = new PdfPCell(phx);
        cell100.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell100.setBorder(PdfPCell.TOP | PdfPCell.RIGHT | PdfPCell.LEFT);
        //   0cell10.setBorderWidth(1.5f);
        cell100.setFixedHeight(40);
        cell100.setPaddingRight(20);
        cell100.setVerticalAlignment(Element.ALIGN_CENTER);
        table001.addCell(cell100);
/*
        cell10 = new PdfPCell(ph);
        cell10.setPaddingLeft(20);
        cell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell10.setBorder(PdfPCell.TOP | PdfPCell.LEFT);
        //   cell10.setBorderWidth(1.5f);
        cell10.setFixedHeight(40);
        cell10.setVerticalAlignment(Element.ALIGN_CENTER);
        table001.addCell(cell10);
*/
        //////////







//////////////
        PdfPTable table00 = new PdfPTable(new float[]{8, 8});
        table00.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table00.getDefaultCell().setFixedHeight(40);
        table00.setTotalWidth(PageSize.A4.getWidth());
        table00.setWidthPercentage(100);
        table00.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table00.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        // Font f = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        //  Font f=new Font(bfBold2,15);
        //    Toast.makeText(Main2Activity.this, ""+f.getSize(), Toast.LENGTH_SHORT).show();
        Phrase ph = new Phrase("");
        ph.add(new Chunk(ARABIC, f));
        ph.add(new Chunk("نام مشتری : محسن سازور"));
        //  document.add(ph);


        PdfPCell cell10 = new PdfPCell(ph);
        cell10.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell10.setBorder(PdfPCell.RIGHT);
        //   cell10.setBorderWidth(1.5f);
        cell10.setFixedHeight(40);
        cell10.setPaddingRight(20);
        cell10.setVerticalAlignment(Element.ALIGN_CENTER);
        table00.addCell(cell10);

        cell10 = new PdfPCell(ph);
        cell10.setPaddingLeft(20);
        cell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell10.setBorder(PdfPCell.LEFT);
        //   cell10.setBorderWidth(1.5f);
        cell10.setFixedHeight(40);
        cell10.setVerticalAlignment(Element.ALIGN_CENTER);
        table00.addCell(cell10);

        //////////















        PdfPTable table0 = new PdfPTable(new float[]{8, 8});
        table0.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table0.getDefaultCell().setFixedHeight(40);
        table0.setTotalWidth(PageSize.A4.getWidth());
        table0.setWidthPercentage(100);
        table0.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table0.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        Phrase ph0 = new Phrase("");
        ph0.add(new Chunk(ARABIC, f));
        ph0.add(new Chunk("آدرس : سیرجان - نجف شهر _ فاز2 "));

        PdfPCell cell1 = new PdfPCell(new Phrase(ph0));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(PdfPCell.BOTTOM | PdfPCell.RIGHT);
        //   cell1.setBorderWidth(1.5f);
        cell1.setFixedHeight(40);
        cell1.setPaddingRight(20);
        cell1.setVerticalAlignment(Element.ALIGN_CENTER);
        table0.addCell(cell1);


        Phrase ph00 = new Phrase("");
        ph00.add(new Chunk(ARABIC, f));
        ph00.add(new Chunk("تلفن : 0913575762 "));


        cell1 = new PdfPCell(new Phrase(ph00));
        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell1.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT);
        //   cell1.setBorderWidth(1.5f);
        cell1.setPaddingLeft(20);
        cell1.setFixedHeight(40);
        cell1.setVerticalAlignment(Element.ALIGN_CENTER);
        table0.addCell(cell1);


        ///



        PdfPTable tablex0 = new PdfPTable(new float[]{8, 8});
        tablex0.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tablex0.getDefaultCell().setFixedHeight(40);
        tablex0.setTotalWidth(PageSize.A4.getWidth());
        tablex0.setWidthPercentage(100);
        tablex0.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        tablex0.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        Phrase phx0 = new Phrase("");
        phx0.add(new Chunk(ARABIC, f));
        phx0.add(new Chunk("شماره فاکتور : 476468587"));

        PdfPCell cellx1 = new PdfPCell(new Phrase(phx0));
        cellx1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cellx1.setBorder(PdfPCell.BOTTOM | PdfPCell.RIGHT);
        //   cell1.setBorderWidth(1.5f);
        cellx1.setFixedHeight(40);
        cellx1.setPaddingRight(20);
        cellx1.setVerticalAlignment(Element.ALIGN_CENTER);
        tablex0.addCell(cellx1);


        Phrase phx00 = new Phrase("");
        phx00.add(new Chunk(ARABIC, f));
        phx00.add(new Chunk("تلفن ثابت : 09189978962 "));


        cellx1 = new PdfPCell(new Phrase(phx00));
        cellx1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellx1.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT);
        //  x cell1.setBorderWidth(1.5f);
        cellx1.setPaddingLeft(20);
        cellx1.setFixedHeight(40);
        cellx1.setVerticalAlignment(Element.ALIGN_CENTER);
        tablex0.addCell(cellx1);


        ///
















        PdfPTable table = new PdfPTable(new float[]{2, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 3, 1});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(25);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);


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
        ph3.add(new Chunk("مقدار"));
        table.addCell(ph3);
        Phrase ph4 = new Phrase("");
        ph4.add(new Chunk(ARABIC, f));
        ph4.add(new Chunk("واحد کل"));
        table.addCell(ph4);


        Phrase ph33 = new Phrase("");
        ph33.add(new Chunk(ARABIC, f));
        ph33.add(new Chunk("مقدار"));
        table.addCell(ph33);
        Phrase ph44 = new Phrase("");
        ph44.add(new Chunk(ARABIC, f));
        ph44.add(new Chunk("واحد جز"));
        table.addCell(ph44);


        Phrase ph5 = new Phrase("");
        ph5.add(new Chunk(ARABIC, f));
        ph5.add(new Chunk("فی"));
        table.addCell(ph5);
        Phrase ph6 = new Phrase("");
        ph6.add(new Chunk(ARABIC, f));
        ph6.add(new Chunk("تخفیف"));
        table.addCell(ph6);

        Phrase ph8 = new Phrase("");
        ph8.add(new Chunk(ARABIC, f));
        ph8.add(new Chunk("جمع کل"));
        table.addCell(ph8);
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.LIGHT_GRAY);
            //   cells[j].setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
            //   cells[j].setBorder(Rectangle.NO_BORDER);
        }

        //   cells[1].setBorderWidth(150);

        for (int i = 0; i < MyList1.size(); i++) {
            num = MyList1.get(i);
            name = MyList1.get(i);
            amount_big = MyList1.get(i);
            unit_big = MyList1.get(i);
            amount_low = MyList1.get(i);
            unit_low = MyList1.get(i);
            fi = MyList1.get(i);
            takhfif = MyList1.get(i);
            total = MyList1.get(i);

            String numn = num.getItem_num();
            String namen = name.getItem_name();
            String amountn_big = amount_big.getItem_amount_big();
            String amountn_low = amount_low.getItem_amount_low();
            String unitn_big = unit_big.getItem_unit_big();
            String unitn_low = unit_low.getItem_unit_low();
            String fin = fi.getItem_fi();
            String takhfifn = takhfif.getItem_takhfif();
            String totaln = total.getItem_total();

            Phrase ph_name = new Phrase("");
            ph_name.add(new Chunk(ARABIC, f));
            ph_name.add(new Chunk(namen));

            Phrase ph_unit = new Phrase("");
            ph_unit.add(new Chunk(ARABIC, f));
            ph_unit.add(new Chunk(unitn_big));

            Phrase ph_unit_low = new Phrase("");
            ph_unit_low.add(new Chunk(ARABIC, f));
            ph_unit_low.add(new Chunk(unitn_low));

            table.addCell(String.valueOf(numn));
            table.addCell(ph_name);
            table.addCell(String.valueOf(amountn_big));
            table.addCell(ph_unit);
            table.addCell(String.valueOf(amountn_low));
            table.addCell(ph_unit_low);
            table.addCell(String.valueOf(fin));
            table.addCell(String.valueOf(takhfifn));
            table.addCell(String.valueOf(totaln));

        }

//        System.out.println("Done");

        //  PdfWriter.getInstance(document, output);
        PdfWriter docWriter = PdfWriter.getInstance(document, output);


        ParagraphBorder border = new ParagraphBorder();
        docWriter.setPageEvent(border);


        document.open();
        PdfContentByte cb = docWriter.getDirectContent();
        ///
/*
        Rectangle rectangle=new Rectangle(36,36,559,806);
        rectangle.setBorder(Rectangle.BOX);
        rectangle.setBorderWidth(1);

        cb.setColorStroke(BaseColor.BLACK);
        cb.rectangle(rectangle);

*/


        document.addAuthor("Sazvar");
        document.addCreationDate();
        document.addProducer();
        document.addCreator("www.sazvar.com");
        document.addTitle("3azvar");
        document.setPageSize(PageSize.A4);
// left,right,top,bottom
        //  document.setMargins(36, 36, 36, 36);
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
        //  document.add(companyLogo);


        //    myfont= null;

        Font parafont = FontFactory.getFont("assets/font/XBZar.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        // Font parafont=new Font(myfont,18);


        ///

        Phrase pc = new Phrase("");
        pc.add(new Chunk(ARABIC, f));
        pc.add(new Chunk("بنام خدا"));
        Paragraph p = new Paragraph(pc);
        p.setSpacingBefore(5);
        p.setSpacingAfter(5);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        //  document.add(p);

        Paragraph p3 = new Paragraph();
        p3.setSpacingBefore(5);
        p3.setSpacingAfter(5);
        p3.setAlignment(PdfWriter.RUN_DIRECTION_RTL);
        // p3.setFont(parafont);
        Phrase ph22 = new Phrase("محسن: ");
        ph22.add(new Chunk(ARABIC, f));
        ph22.add(new Chunk("علیرضایی"));
        p3.add(ph22);
        //     document.add(p3);



/*
        DottedLineSeparator separator = new DottedLineSeparator();
        separator.setPercentage(59500f / 523f);
        Chunk linebreak = new Chunk(separator);
        document.add(linebreak);
*/


        document.add(new Paragraph("\u0418", parafont));

        //   document.add(new LineSeparator());


        //  LineSeparator sep = new LineSeparator();
        // sep.setOffset(5);
        // document.add(sep);


        //  document.add(new Paragraph("فاکتور: \u0418", parafont));
        //document.add(new Paragraph(Farsi.Convert("تبدیل"), parafont));
        //  Font f = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.UNDERLINE, BaseColor.BLACK);


        //    document.add(new Paragraph("date : 1398/7/9"));


        initializeFonts();

        //     createHeadings(cb,400,760,"کشور"+"\u200F");
        ////    createHeadings(cb,400,745,"Address Line 1");
        //    createHeadings(cb,400,730,"آدرس");
        //    createHeadings(cb,400,715,"شهر");

        String f2 = "فاکتور فروش او";
        String ff = "";
        try {
            ff = new String(f2.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //  createHeadings(cb,250,780,ff);


        //  createHeadings(cb,35,760,"1111"+"\u200F");
        //    createHeadings(cb,35,745,"222");
        //    createHeadings(cb,35,730,"333");
        //    createHeadings(cb,35,715,"444");


        ColumnText ct = new ColumnText(cb);
        ct.setSimpleColumn(0f, 0f, 600f, 850f);
        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        ct.setAlignment(Paragraph.ALIGN_CENTER);
        Phrase ph55 = new Phrase("");
        ph55.add(new Chunk(ARABIC, f));
        ph55.add(new Chunk("فاکتور فروش علی"));


        Paragraph pac = new Paragraph(ph55);
        pac.setAlignment(Paragraph.ALIGN_CENTER);
        ct.addElement(pac);
//ct.go();

        ColumnText ct2 = new ColumnText(cb);
        ct.setSimpleColumn(0f, 0f, 450f, 700f);
        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        ct.setAlignment(Paragraph.ALIGN_CENTER);
        Phrase ph555 = new Phrase("");
        ph555.add(new Chunk(ARABIC, f));
        ph555.add(new Chunk("فاکتور علی"));


        Paragraph pa5 = new Paragraph(ph555);
        pa5.setAlignment(Paragraph.ALIGN_CENTER);
        ct.addElement(pa5);
//ct.go();


// step 4

// state 1:
        //cb.setRGBColorFill(0xFF, 0x45, 0x00);

// fill a rectangle in state 1
        //  cb.rectangle(10, 10, 60, 60);
        //   cb.fill();
        //   cb.saveState();


//dash line
        /*
        DottedLineSeparator separator2 = new DottedLineSeparator();
        separator.setPercentage(59500f / 523f);
        Chunk linebreak2 = new Chunk(separator2);
        document.add(linebreak2);
*/


        document.add(table001);
        document.add(table00);
        document.add(table0);
        document.add(tablex0);
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




























/*
        ColumnText ct = new ColumnText(cb);
        ct.setSimpleColumn(120f, 500f, 250f, 780f);
        Paragraph pr = new Paragraph("This is a long paragraph that doesn't"
                + "fit the width we defined for the simple column of the"
                + "ColumnText object, so it will be distributed over several"
                + "lines (and we don't know in advance how many).");
        ct.addElement(pr);
        ct.go();

*/


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


//        for (int i = 0; i < MyList1.size(); i++) {
//            document.add(new Paragraph(String.valueOf(MyList1.get(i))));
//        }
        document.close();
        Log.e("safiya", MyList1.toString());
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
                //   Connection con = connectionClass.CONN();

                //  String query = "select * from youtable";
                // Statement statement = con.createStatement();
                //  ResultSet rs = statement.executeQuery(query);
                MyList1 = new ArrayList<GiftitemPOJO>();
                //    while (rs.next()) {
                for (int i = 0; i < 25; i++) {

                    giftitemPOJO.setItem_num((i + 1) + "");
                    giftitemPOJO.setItem_name("تاید دستی دریا ( عددی 40)");
                    giftitemPOJO.setItem_amount_big("500");
                    giftitemPOJO.setItem_unit_big("کیلوگرم");
                    giftitemPOJO.setItem_amount_low("50.5");
                    giftitemPOJO.setItem_unit_low("گرم");
                    giftitemPOJO.setItem_fi("3000");
                    giftitemPOJO.setItem_takhfif("1500");
                    giftitemPOJO.setItem_total("1650000");
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
    private BaseFont bfBold2;

    private void createHeadings(PdfContentByte cb, float x, float y, String text) {
        cb.beginText();
        cb.setFontAndSize(bfBold, 20);
        cb.setTextMatrix(x, y);
        cb.showText(text.trim());
        cb.endText();

    }


    private void initializeFonts() {


        try {
            bfBold = BaseFont.createFont("assets/font/XBZar.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            //   bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            //  myfont = BaseFont.createFont("assets/font/IRANSansMobile.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ParagraphBorder extends PdfPageEventHelper {
        public boolean active = false;

        public void setActive(boolean active) {
            this.active = active;
        }

        public float offset = 5;
        public float startPosition;

        @Override
        public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
            this.startPosition = paragraphPosition;
        }

        @Override
        public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
            if (active) {
                PdfContentByte cb = writer.getDirectContentUnder();
                cb.rectangle(document.left(), paragraphPosition - offset,
                        document.right() - document.left(), startPosition - paragraphPosition);
                cb.stroke();
            }
        }
    }


}