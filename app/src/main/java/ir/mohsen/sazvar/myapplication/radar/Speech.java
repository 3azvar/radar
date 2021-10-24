package ir.mohsen.sazvar.myapplication.radar;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import java.util.ArrayList;
import java.util.Locale;

public class Speech extends AppCompatActivity implements RecognitionListener {

    private static final int REQUEST_RECORD_PERMISSION = 1100;
    private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";

    public final int MY_REQUEST_CODE = 1;
    Button btnstt;
    TextView txt;
    TextToSpeech tts;
    public String matn = "";
    public String matn2 = "";
    public ArrayList<String> jomle = new ArrayList<String>();
    public ArrayList<String> joml = new ArrayList<String>();
/////
Physicaloid mPhysicaloid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech);
        mPhysicaloid = new Physicaloid(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);


        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        //  Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (Speech.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);

                    onClick_btn_Open(buttonView);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });


        txt = (TextView) findViewById(R.id.txt);
        btnstt = (Button) findViewById(R.id.stt);

        if (Build.VERSION.SDK_INT >= 23) {
         //   checkMyPermissions();
        }


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    int result = tts.setLanguage(Locale.ENGLISH);
                    tts.speak("hello", TextToSpeech.QUEUE_FLUSH, null);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "This language not supported ....", Toast.LENGTH_SHORT).show();

                        // btnSpeak.setEnabled(false);
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Init failed ...", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btnstt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "هر چی دوست داری بگو :");
                try {
                    startActivityForResult(intent, 100);

                } catch (ActivityNotFoundException e) {

                    Toast.makeText(getApplicationContext(), "دستگاه فاقد برنامه مورد نظر است", Toast.LENGTH_SHORT).show();

                }
            }
        });


        matn = "";


        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getfilename("سلام چه خبر");
                for (int i = 0; i < jomle.size(); i++) {

                    matn2 = matn2 + jomle.get(i);
                    //  matn=word+matn;

                }
                tts.speak(matn2, TextToSpeech.QUEUE_FLUSH, null);
                // Toast.makeText(getApplicationContext(),""+matn,Toast.LENGTH_SHORT).show();
                jomle.clear();
                jomle.removeAll(jomle);
                matn = "";
            }


        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {

            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txt.setText(result.get(0));
                getfilename(result.get(0));
                for (int i = 0; i < jomle.size(); i++) {

                    matn2 = matn2 + jomle.get(i);
                    //  matn=word+matn;

                }
                tts.speak(matn2, TextToSpeech.QUEUE_FLUSH, null);
                // Toast.makeText(getApplicationContext(),""+matn,Toast.LENGTH_SHORT).show();
                jomle.clear();
                jomle.removeAll(jomle);
                matn2 = "";


            }

        }
    }


    public void getfilename(String address) {
        String[] name = address.split(" ");
        for (String word : name) {

            switch (word) {


                case "سلام":
                    jomle.add(" salam ");
                    break;
                case "هوا":
                    jomle.add(" ara ");

                    break;
                case "اهل":
                    jomle.add(" ahle seir jan ");
                    break;

                case "کجا":
                    jomle.add(" i am from seir jan ");

                    break;

                case "زندگی":
                    jomle.add(" i am from seir jan ");

                    break;
                case "دوست":

                    jomle.add("taha hast doste abol fazl ");

                    break;
                case "حالت":
                    jomle.add(" bale ");

                    break;
                case "خوش":
                    jomle.add(" yes i am ok ");
                    break;
                case "حال":
                    jomle.add(" i am very good ");
                    break;
                case "خوبی":
                    jomle.add(" tankful i am very good ");
                    break;

                case "خوشی":
                    jomle.add(" tankful i am very good ");
                    break;
                case "بهتری":
                    jomle.add(" tankful i am very good ");
                    break;
                case "چطوری":
                    jomle.add(" tankful i am very good ");
                    break;
                case "خبر":
                    jomle.add(" wellness is not a special news ");
                    break;
                case "کار":
                    jomle.add(" noting i am unempioyed ");
                    break;
                case "چیکار":
                    jomle.add(" noting i am unempioyed ");
                    break;
                case "سلامتی":
                    jomle.add(" tanks ");
                    break;
                case "احوال":
                    jomle.add(" i am very good ");
                    break;
                case "اسمت":
                    jomle.add(" esme man hast taha ");
                    break;
                case "اسم":
                    jomle.add(" esme man hast fa tame ");
                    break;
                case "سازنده":
                    jomle.add(" is my builder mawsen salz var ");
                    break;
                case "ساخته":
                    jomle.add(" is my builder mawsen salz var ");
                    break;
                case "سازندت":
                    jomle.add(" is my builder mawsen salz var ");
                    break;
                case "بالا":
                    jomle.add(" bala ");
                    break;
                case "پایین":
                    jomle.add(" payin ");
                    break;
                case "چپ":
                    jomle.add(" chap ");
                    break;
                case "راست":
                    jomle.add(" raast ");
                    break;
                case "چرخش":
                    jomle.add(" charkhesh ");
                    break;
                case "دور":
                    jomle.add(" dour ");
                    break;
                case "عقب":
                    jomle.add(" aghab ");
                    break;
                case "شعر":
                    jomle.add(" seh ta se taaree dar dastam oftad boodand an ha bes yar ziba yak daane ash ra dadam be  ba ba an digary ra dadam be maadar didam man de yek daane digar an ra be baala paar taab kaar dam in kar ha ra dar khab kaar dam naser ke shavarzi ");
                    break;

            }


        }


    }
    public String getfilename2(String address) {
        String[] name = address.split(" ");
        String txt="";
        for (String word : name) {

            switch (word) {


                case "سلام":
                    txt = " salam ";
                    break;
                case "یک":
                    txt =("one");

                    byte[] buf1 = ("J").getBytes();
                    mPhysicaloid.write( buf1 );
                    stop();
                    break;
                case "سه":
                    txt =("three");

                    byte[] buf2 = ("I").getBytes();
                    mPhysicaloid.write( buf2 );
                    stop();
                    break;
                case "هفت":
                    txt =("seven");
                    byte[] buf3 = ("K").getBytes();
                    mPhysicaloid.write( buf3 );
                    stop();
                    break;
                case "نه":
                    txt =("nine");
                    byte[] buf4 = ("M").getBytes();
                    mPhysicaloid.write( buf4 );
                    stop();
                    break;
                case "بالا":
                    txt =(" bala ");
                    byte[] buf5 = ("F").getBytes();
                    mPhysicaloid.write( buf5 );
                    stop();
                    break;
                case "جلو":
                    txt =(" jelo ");
                    byte[] buf6 = ("F").getBytes();
                    byte[] buf66 = ("S").getBytes();
                    mPhysicaloid.write( buf6 );
                    mPhysicaloid.write( buf66 );
                    break;
                case "پایین":
                    txt =(" payin ");
                    byte[] buf7 = ("B").getBytes();
                    mPhysicaloid.write( buf7 );
                    stop();
                    break;
                case "چپ":
                    txt =(" chap ");
                    byte[] buf8 = ("L").getBytes();
                    mPhysicaloid.write( buf8 );
                    stop();
                    break;
                case "راست":
                    txt =(" raast ");
                    byte[] buf9 = ("R").getBytes();
                    mPhysicaloid.write( buf9 );
                    stop();
                    break;
                case "چرخش":
                    txt =(" charkhesh ");
                    byte[] buf10 = ("IM").getBytes();
                    mPhysicaloid.write( buf10 );
                    stop();
                    break;
                case "دور":
                    txt =(" door ");
                    byte[] buf11 = ("I").getBytes();
                    mPhysicaloid.write( buf11 );
                    byte[] buf12 = ("M\n").getBytes();
                    mPhysicaloid.write( buf12 );
                    stop();
                    break;
                case "عقب":
                    txt =(" aghab ");
                    byte[] buf = ("B").getBytes();
                    mPhysicaloid.write( buf );
                    stop();
                    break;
                case "توقف":
                    txt =(" stop ");
                    byte[] bufs = ("S").getBytes();
                    mPhysicaloid.write( bufs );
                    stop();
                    break;





                case "اهل":
                    txt =(" ahle iran ");
                    break;

                case "کجا":
                    txt =(" i am from iran ");

                    break;

                case "زندگی":
                    txt =(" i am from iran ");

                    break;
                case "ازدواج":
                    txt =(" not find good case ");

                    break;
                case "دوست":

                    txt =("taha va  abol fazl ");

                    break;
                case "حالت":
                    txt =(" good ");

                    break;
                case "خوش":
                    txt =(" yes i am ok ");
                    break;
                case "آفرین":
                    txt =(" merc ");
                    break;
                case "بگو":
                    txt =(" chi ");
                    break;
                case "حال":
                    txt =(" i am very good ");
                    break;
                case "خوبی":
                    txt =(" tankful i am very good ");
                    break;

                case "خوشی":
                    txt =(" thank you i am fine ");
                    break;
                case "بهتری":
                    txt =(" yeeh ");
                    break;
                case "چطوری":
                    txt =(" very good ");
                    break;
                case "خبر":
                    txt =(" salamati ");
                    break;
                case "کار":
                    txt =(" bi car ");
                    break;
                case "چیکار":
                    txt =(" bi car ");
                    break;
                case "سلامتی":
                    txt =(" tanks ");
                    break;
                case "احوال":
                    txt =(" i am very good ");
                    break;
                case "اسمت":
                    txt =(" esme man hast robot ");
                    break;
                case "اسم":
                    txt =(" esme man hast robot ");
                    break;
                case "سازنده":
                    txt =(" is my builder mawsen salz var ");
                    break;
                case "ساخته":
                    txt =(" is my builder mawsen salz var ");
                    break;
                case "سازندت":
                    txt =(" is my builder mawsen salz var ");
                    break;

                case "تند تر":
                    txt =(" toundtar ");
                    break;
                case "برو":
                    txt =(" ok ");
                    break;
                case "بیا":
                    txt =(" ok ");
                    break;
                case "آهسته":
                    txt =(" slow ");
                    break;
                case "مستقیم":
                    txt =(" alright ");
                    break;
                case "محسن":
                    txt =(" mohsen is very good ");
                    break;
                case "میشنوی":
                    txt =(" yes ");
                    break;
                case "سریعتر":
                    txt =(" faster ");
                    break;
                case "میخوری":
                    txt =(" no ");
                    break;
                case "غذا":
                    txt =(" no ");
                    break;
                case "گوش":
                    txt =(" no ");
                    break;
                case "قشنگ":
                    txt =(" yes ");
                    break;
                case "ساکت":
                    txt =(" bi adab ");
                    break;
                case "روشنی":
                    txt =(" yes ");
                    break;
                case "شعر":
                    txt =(" seh ta se taaree dar dastam oftad boodand an ha bes yar ziba yak daane ash ra dadam be  ba ba an digary ra dadam be maadar didam man de yek daane digar an ra be baala paar taab kaar dam in kar ha ra dar khab kaar dam naser ke shavarzi ");
                    break;
                default:txt = "";
            }


        }
return txt;

    }


    private void checkMyPermissions() {

        if (ContextCompat.checkSelfPermission(Speech.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Speech.this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, MY_REQUEST_CODE);


        } else {
            //Toast.makeText(Speech.this, "mojaz shode", Toast.LENGTH_SHORT).show();
            //getContacts();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case MY_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Speech.this, " shode", Toast.LENGTH_SHORT).show();
                    // getContacts();
                } else {
                    //  Toast.makeText(Test.this, "mojaz n", Toast.LENGTH_SHORT).show();
                }

            }


            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(Speech.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            // Log.i(LOG_TAG, "destroy");
        }
    }


    @Override
    public void onBeginningOfSpeech() {
        //Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        //  Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        //  Log.i(LOG_TAG, "onEndOfSpeech");
      //  progressBar.setIndeterminate(true);
        //toggleButton.setChecked(false);
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onError(int errorCode) {
       // String errorMessage = getErrorText(errorCode);
        // Log.d(LOG_TAG, "FAILED " + errorMessage);
       // returnedText.setText(errorMessage);
        //toggleButton.setChecked(false);
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        // Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        //  Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        // Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        //  Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
        {    text += result + "\n";

        String re=getfilename2(result);
        if (!re.equals("")){
            tts.speak(re, TextToSpeech.QUEUE_FLUSH, null);
        }}
        returnedText.setText(text);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //  Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( mPhysicaloid.open() ) {
            mPhysicaloid.close();
        }
    }

    public void onClick_btn_Open(View v ) {

        UartConfig uartConfig = new UartConfig();

        // DATA_BITS7 = 7, DATA_BITS8 = 8
        // PARITY_NONE = 0, PARITY_ODD = 1, PARITY_EVEN = 2, PARITY_MARK = 3, PARITY_SPACE = 4;
        // STOP_BITS1 = 0, STOP_BITS1_5 = 1, STOP_BITS2 = 2
        // FLOW_CONTROL_OFF = 0, FLOW_CONTROL_ON = 1;

        uartConfig.baudrate = 9600;
        uartConfig.dataBits = 8; // DATA_BITS8
        uartConfig.parity   = 0; // PARITY_NONE
        uartConfig.stopBits = 0; // STOP_BITS1

        mPhysicaloid.setConfig( uartConfig );

        if( mPhysicaloid.open() ) {

            Toast.makeText(this, "USB Baz Shod !", Toast.LENGTH_SHORT).show();
            mPhysicaloid.addReadListener(new ReadLisener() {
                @Override
                public void onRead(int size) {
                    byte[] buf = new byte[size];
                  //  mPhysicaloid.read(buf, size);
                  //  tvAppend(txtview, new String(buf) );
                }
            });
        }
        else {

            if( mPhysicaloid.isOpened() ) {
                Toast.makeText(this, "USB Gablan Baz Shode !", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "USB Baz Nashod !", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void onClick_btn_Close(View v) {
        if( mPhysicaloid.isOpened() ) {

            if( mPhysicaloid.close() ) {
                mPhysicaloid.clearReadListener();
                Toast.makeText(this, "USB Baste Shod !", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "USB Baste Nashod !!!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "USB Qablan Baz Nabode !!!", Toast.LENGTH_SHORT).show();
        }
    }
    private void stop(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                byte[] buf = ("S").getBytes();
                mPhysicaloid.write( buf );
            }
        },600);
    }
}