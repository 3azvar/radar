package ir.mohsen.sazvar.myapplication.radar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    int dX, dY, downRawX, downRawY;
    RelativeLayout linfab, lin2;
    ImageView abi, pink, lin3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linfab = (RelativeLayout) findViewById(R.id.rel1);
        lin2 = (RelativeLayout) findViewById(R.id.rel2);
        lin3 = (ImageView) findViewById(R.id.rel3);
        abi = (ImageView) findViewById(R.id.imgabi);
        pink = (ImageView) findViewById(R.id.imgpink);
        move(linfab);
        move(lin2);

        Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);

        abi.startAnimation(animBlink);


        Animation heart = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.heartbeat);

        pink.startAnimation(heart);


        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);

        lin3.startAnimation(rotate);


    }


    public void move(View vi) {


        vi.setOnTouchListener(new View.OnTouchListener() {

            @Override

            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {

                    downRawX = (int) event.getRawX();
                    downRawY = (int) event.getRawY();
                    dX = (int) v.getX() - downRawX;
                    dY = (int) v.getY() - downRawY;

                    return true; // Consumed

                } else if (action == MotionEvent.ACTION_MOVE) {

                    int viewWidth = v.getWidth();
                    int viewHeight = v.getHeight();

                    View viewParent = (View) v.getParent();
                    int parentWidth = viewParent.getWidth();
                    int parentHeight = viewParent.getHeight();

                    float newX = event.getRawX() + dX;
                    newX = Math.max(0, newX); // Don't allow the FAB past the left hand side of the parent
                    newX = Math.min(parentWidth - viewWidth, newX); // Don't allow the FAB past the right hand side of the parent

                    float newY = event.getRawY() + dY;
                    newY = Math.max(0, newY); // Don't allow the FAB past the top of the parent
                    newY = Math.min(parentHeight - viewHeight, newY); // Don't allow the FAB past the bottom of the parent

                    v.animate()
                            .x(newX)
                            .y(newY)
                            .setDuration(0)
                            .start();
                    //  Toast.makeText(MainActivity.this, newX+""+newY, Toast.LENGTH_SHORT).show();
                    return true; // Consumed

                } else if (action == MotionEvent.ACTION_UP) {

                    float upRawX = event.getRawX();
                    float upRawY = event.getRawY();
                    float upDX = upRawX - downRawX;
                    float upDY = upRawY - downRawY;
                    return true; // Consumed

                } else {
                    return false;
                }

            }
        });


    }
}
