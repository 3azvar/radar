package ir.mohsen.sazvar.myapplication.radar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceRecogSpeech extends Activity implements
        RecognitionListener {

    private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognition";
    String speechString = "";
    boolean spechStarted = false;
Button btngo;
    TextView txt;
    TextToSpeech tts;
    public String matn="";
    public String matn2="";
    public ArrayList<String> jomle= new ArrayList<String>();
    public   ArrayList<String> joml = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        returnedText = (TextView) findViewById(R.id.textview1);
        progressBar = (ProgressBar) findViewById(R.id.progress1);
        toggleButton = (ToggleButton) findViewById(R.id.toggle1);
btngo=(Button) findViewById(R.id.btngo);
btngo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent i=new Intent(VoiceRecogSpeech.this,Speech.class);
        startActivity(i);
    }
});
        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "fa");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true);

        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    speech.setRecognitionListener(VoiceRecogSpeech.this);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    speech.startListening(recognizerIntent);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                    speech.destroy();

                }
            }
        });


        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status==TextToSpeech.SUCCESS) {

                    int result = tts.setLanguage(Locale.ENGLISH);
                    tts.speak("hello",TextToSpeech.QUEUE_FLUSH,null);
                    if (result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Toast.makeText(getApplicationContext(),"This language not supported ....",Toast.LENGTH_SHORT).show();

                        // btnSpeak.setEnabled(false);
                    }



                }else
                {
                    Toast.makeText(getApplicationContext(),"Init failed ...",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

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
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        spechStarted = true;
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {

        spechStarted = false;
        Log.i(LOG_TAG, "onEndOfSpeech");
        speech.startListening(recognizerIntent);

    }

    @Override
    public void onError(int errorCode) {
        Log.d(LOG_TAG, "FAILED ");
        if (!spechStarted)
            speech.startListening(recognizerIntent);

    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");

        ArrayList<String> matches = arg0
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        returnedText.setText(speechString + matches.get(0));
        getfilename(matches.get(0));
        for (int i=0;i<jomle.size();i++){

            matn2=matn2+jomle.get(i);
            //  matn=word+matn;

        }
        tts.speak(matn2,TextToSpeech.QUEUE_FLUSH,null);
        // Toast.makeText(getApplicationContext(),""+matn,Toast.LENGTH_SHORT).show();
        jomle.clear();
        jomle.removeAll(jomle);
        matn2="";


    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
     //   Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        speechString = speechString + ". " + matches.get(0);
        ArrayList<String> result=results.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        returnedText.setText(result.get(0));
        getfilename(result.get(0));
        for (int i=0;i<jomle.size();i++){

            matn2=matn2+jomle.get(i);
            //  matn=word+matn;

        }
        tts.speak(matn2,TextToSpeech.QUEUE_FLUSH,null);
        // Toast.makeText(getApplicationContext(),""+matn,Toast.LENGTH_SHORT).show();
        jomle.clear();
        jomle.removeAll(jomle);
        matn2="";


    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }



    public void getfilename(String address){
        String[] name=address.split(" ");
        for (String word :name) {

            switch (word){


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
                    jomle.add(" esme man hast robot ");
                    break;
                case "اسم":
                    jomle.add(" esme man hast robot ");
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

                case "طاها":
                    jomle.add(" taha pesar goli ast ");
                    break;

                case "اینجا":
                    jomle.add(" inja hast haal ");
                    break;
                case "تندتر":
                    jomle.add(" bishtar az in namishe ");
                    break;
                case "دما":
                    jomle.add(" dama hava hast si o se daraje ");
                    break;
                case "شعر":
                    jomle.add(" seh ta se taaree dar dastam oftad boodand an ha bes yar ziba yak daane ash ra dadam be  ba ba an digary ra dadam be maadar didam man de yek daane digar an ra be baala paar taab kaar dam in kar ha ra dar khab kaar dam naser ke shavarzi ");
                    break;

            }




        }



    }





}