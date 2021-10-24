package ir.mohsen.sazvar.myapplication.radar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

public class Arduino extends Activity implements SensorEventListener{
Button b1,b2,b3,b4;
    Spinner sp_BaudRate;
    Physicaloid mPhysicaloid;
    TextView txtview;
    Typeface font ;
    private ImageView mPointer;
    public static StringBuilder stringBuilder=new StringBuilder();
    static ToggleButton Tbtn_TurnOnOff_LED;
//////
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private int mCurrentDegree = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino);
        font = Typeface.createFromAsset(getAssets(), "font/digitit.ttf");
        b1=(Button) findViewById(R.id.a1);
        b2=(Button) findViewById(R.id.a2);
        b3=(Button) findViewById(R.id.a3);
        b4=(Button) findViewById(R.id.a4);
        txtview=(TextView) findViewById(R.id.txt);

        b1.setTypeface(font);
        b2.setTypeface(font);
        b3.setTypeface(font);
        b4.setTypeface(font);
        txtview.setTypeface(font);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.pointer);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTodegree();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] buf = ("L").getBytes();
                mPhysicaloid.write( buf );
                Tbtn_TurnOnOff_LED.setText("number 2");
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Arduino.this,Speech.class);
                startActivity(i);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] buf = ("B").getBytes();
                mPhysicaloid.write( buf );
                Tbtn_TurnOnOff_LED.setText("number 4");
            }
        });

        sp_BaudRate = (Spinner) findViewById(R.id.sp_BaudRate);
        sp_BaudRate.setSelection(4); // 9600

        Tbtn_TurnOnOff_LED = (ToggleButton)findViewById(R.id.Tbtn_TurnOnOff_LED);
        Tbtn_TurnOnOff_LED.setChecked(false);
        Tbtn_TurnOnOff_LED.setText("LED ????");

        mPhysicaloid = new Physicaloid(this);
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

        switch ( sp_BaudRate.getSelectedItem().toString() ) {
            case "300"     :   uartConfig.baudrate = 300;      break;
            case "1200"    :   uartConfig.baudrate = 1200;     break;
            case "2400"    :   uartConfig.baudrate = 2400;     break;
            case "4800"    :   uartConfig.baudrate = 4800;     break;
            case "9600"    :   uartConfig.baudrate = 9600;     break;
            case "19200"   :   uartConfig.baudrate = 19200;    break;
            case "38400"   :   uartConfig.baudrate = 38400;    break;
            case "576600"  :   uartConfig.baudrate = 576600;   break;
            case "744880"  :   uartConfig.baudrate = 744880;   break;
            case "115200"  :   uartConfig.baudrate = 115200;   break;
            case "230400"  :   uartConfig.baudrate = 230400;   break;
            case "250000"  :   uartConfig.baudrate = 250000;   break;
            default        :   uartConfig.baudrate = 9600;
        }
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
                    mPhysicaloid.read(buf, size);
                //    tvAppend(txtview, new String(buf) );
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

    public void onClick_Tbtn_TurnOnOff_LED( View v ) {

        if( !mPhysicaloid.isOpened() ) {
            Toast.makeText(this, "USB Baz Nist !!!", Toast.LENGTH_SHORT).show();

            Tbtn_TurnOnOff_LED.setChecked(false);

            if( Tbtn_TurnOnOff_LED.isChecked() )    Tbtn_TurnOnOff_LED.setText("LED ????");
            else                                    Tbtn_TurnOnOff_LED.setText("LED ????");

            return;
        }

        if( Tbtn_TurnOnOff_LED.isChecked() ) {
            byte[] buf = ("TO\n").getBytes();
            mPhysicaloid.write( buf );

            Tbtn_TurnOnOff_LED.setText("LED is ON");
        }
        else {
            byte[] buf = ("TF\n").getBytes();
            mPhysicaloid.write( buf );

            Tbtn_TurnOnOff_LED.setText("LED is OFF");
        }
    }
    Handler mHandler = new Handler();
    private void tvAppend(final TextView tv, final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stringBuilder.append(text.toString());
                if (stringBuilder.length()>=25){
                tv.setText(stringBuilder);
                stringBuilder.delete(0, stringBuilder.length());}
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            int azimuthInDegress = (int)(Math.toDegrees(azimuthInRadians)+360)%360;
            txtview.setText(mCurrentDegree+"");
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(100);

            ra.setFillAfter(true);

            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }


     boolean running=true;
  public void goTodegree(){
    byte[] buf = ("R").getBytes();
    mPhysicaloid.write(buf);
    running=true;

        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    String txt = txtview.getText().toString().replace("-", "");
                    if (txt.equals("0") || txt.equals("360")|| txt.equals("359")) {
                        byte[] buf = ("S").getBytes();
                        mPhysicaloid.write(buf);
            running=false;
                    } else {

                    }
                }
            }
        });
        thread.start();
}


}
