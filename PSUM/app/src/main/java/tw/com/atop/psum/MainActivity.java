package tw.com.atop.psum;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.anastr.speedviewlib.PointerSpeedometer;

public class MainActivity extends AppCompatActivity {
    SpeedView speedView;
    AwesomeSpeedometer awesomeSpeedometer;
    PointerSpeedometer pointerSpeedometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedView = (SpeedView)findViewById(R.id.speedView);
        awesomeSpeedometer = (AwesomeSpeedometer)findViewById(R.id.awesomeSpeedometer);
        pointerSpeedometer = (PointerSpeedometer)findViewById(R.id.pointerSpeedometer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float rand = (float)(Math.random() * 5000);
        if(rand > 1000)
        {
            speedView.setUnit("kW");
            speedView.setMaxSpeed(50);
            rand = rand/1000;
        }
        else
        {
            speedView.setUnit("W");
            speedView.setMaxSpeed(1000);
        }
        speedView.speedTo(rand);
        awesomeSpeedometer.speedTo(rand);
        pointerSpeedometer.speedTo(rand);
        return super.onTouchEvent(event);
    }
}
