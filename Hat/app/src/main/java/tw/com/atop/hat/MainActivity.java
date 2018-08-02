package tw.com.atop.hat;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import de.nitri.gauge.Gauge;


public class MainActivity extends AppCompatActivity {
    AnimationDrawable wifiAnimation;
    int curValue = 0;
    Gauge gauge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView)findViewById(R.id.image);
        imageView.setBackgroundResource(R.drawable.animation);
        wifiAnimation = (AnimationDrawable) imageView.getBackground();
        gauge = (Gauge) findViewById(R.id.gauge);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        wifiAnimation.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        wifiAnimation.stop();
        curValue = curValue + 1;
        gauge.moveToValue(curValue);
        return super.onTouchEvent(event);

    }
}
