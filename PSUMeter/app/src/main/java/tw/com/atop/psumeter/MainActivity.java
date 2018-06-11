package tw.com.atop.psumeter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.nitri.gauge.Gauge;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    public DatabaseReference myRef;
    private TextView mFirebaseTextView;
    private TextView p0TextView;
    private TextView p1TextView;
    private TextView p2TextView;
    private TextView ptTextView;
    private TextView e0TextView;
    private TextView e1TextView;
    private TextView e2TextView;
    private TextView etTextView;
    private TextView peakaView;
    private TextView peakbView;
    private TextView peakcView;
    private GestureDetectorCompat gestureDetector;
    //https://www.youtube.com/watch?v=zsNpiOihNXU
    private static final String TAG = "DB0x01";
    private String meterID = "1";
    private EditText NID;
    private Button connect_btn, logout_btn;
    private Gauge gauge;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://drive.google.com/drive/u/0/shared-with-me";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                Snackbar.make(view, "Go to google drive", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mFirebaseTextView = (TextView) findViewById(R.id.firebaseTextView);
        p0TextView = (TextView) findViewById(R.id.p0_txt);
        p1TextView = (TextView) findViewById(R.id.p1_txt);
        p2TextView = (TextView) findViewById(R.id.p2_txt2);
        ptTextView = (TextView) findViewById(R.id.ptotal_txt);


        e0TextView = (TextView) findViewById(R.id.e0_txt);
        e1TextView = (TextView) findViewById(R.id.e1_txt);
        e2TextView = (TextView) findViewById(R.id.e2_txt);
        etTextView = (TextView) findViewById(R.id.et_txt);
        peakaView = (TextView)findViewById(R.id.peak0_txt);
        peakbView = (TextView)findViewById(R.id.peak1_txt);
        peakcView = (TextView) findViewById(R.id.peak2_txt);
        NID = (EditText) findViewById(R.id.NID);
        connect_btn = (Button) findViewById(R.id.connect_btn);
        gauge = (Gauge) findViewById(R.id.gauge);
        logout_btn = (Button) findViewById(R.id.logout_btn);

        //Init Firebase
        auth = FirebaseAuth.getInstance();


        // Get firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        final ValueEventListener codemobiles = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Debug
                Map map = (Map) dataSnapshot.getValue();
                String data = String.valueOf(map.get("debug"));
                mFirebaseTextView.setText(data);

                //Get power
                Map power = (Map) dataSnapshot.child(meterID + "/power").getValue();
                String p0 = String.valueOf(power.get("p0"));
                String p1 = String.valueOf(power.get("p1"));
                String p2 = String.valueOf(power.get("p2"));
                float pt_num = Float.parseFloat(p0) + Float.parseFloat(p1) + Float.parseFloat(p2);
                Log.d(TAG, "showData: p0: " + p0);
                Log.d(TAG, "showData: p1: " + p1);
                Log.d(TAG, "showData: p2: " + p2);
                p0TextView.setText("PA: " + p0 + " W");
                p1TextView.setText("PB: " + p1 + " W");
                p2TextView.setText("PC: " + p2 + " W");
                ptTextView.setText("Pt: " + pt_num + " W");

                //Get energy
                Map energy = (Map) dataSnapshot.child(meterID + "/energy").getValue();
                String e0 = String.valueOf(energy.get("tp0"));
                String e1 = String.valueOf(energy.get("tp1"));
                String e2 = String.valueOf(energy.get("tp2"));
                String et = String.valueOf(energy.get("total"));
                Log.d(TAG, "showData: e0: " + e0);
                Log.d(TAG, "showData: e1: " + e1);
                Log.d(TAG, "showData: e2: " + e2);
                Log.d(TAG, "showData: et: " + et);
                double e0_num = Double.valueOf(e0);
                double e1_num = Double.valueOf(e1);
                double e2_num = Double.valueOf(e2);
                double et_num = Double.valueOf(et);
                e0_num = Double.parseDouble(String.format("%.2f", e0_num));
                e1_num = Double.parseDouble(String.format("%.2f", e1_num));
                e2_num = Double.parseDouble(String.format("%.2f", e2_num));
                et_num = Double.parseDouble(String.format("%.2f", et_num));

                e0TextView.setText("EA: " + String.valueOf(e0_num) + " kWh");
                e1TextView.setText("EB: " + String.valueOf(e1_num) + " kWh");
                e2TextView.setText("EC: " + String.valueOf(e2_num) + " kWh");
                etTextView.setText("ET: " + String.valueOf(et_num) + " kWh");

                // Get peak
                Map peak = (Map) dataSnapshot.child("Peak").getValue();
                String peak0 = String.valueOf(peak.get(meterID + "_PA"));
                String peak1 = String.valueOf(peak.get(meterID + "_PB"));
                String peak2 = String.valueOf(peak.get(meterID + "_PC"));
                peakaView.setText("PeakA: " + peak0 + " W");
                peakbView.setText("PeakB: " + peak1 + " W");
                peakcView.setText("PeakC: " + peak2 + " W");

                gauge.setValue(pt_num);

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        connect_btn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View view){

                        Map<String, Object> value = new HashMap<String, Object>();
                        value.put("debug", "DB0x01");
                        myRef.updateChildren(value);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                        toast.makeText(MainActivity.this, NID.getText(), toast.LENGTH_SHORT).show();
                        meterID = NID.getText().toString();
                        mFirebaseTextView.setText(meterID);
                        NID.setEnabled(false);

                    }
                }
        );

        logout_btn.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "LogoutClicked");
                        logoutUser();
                        myRef.removeEventListener(codemobiles);
                    }
                }
        );


        this.gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        NID.setEnabled(true);NID.setEnabled(true);
        mFirebaseTextView.setText("onSingleTapConfirmed");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mFirebaseTextView.setText("onDoubleTap");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        mFirebaseTextView.setText("onDoubleTapEvent");
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mFirebaseTextView.setText("onDowp");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        mFirebaseTextView.setText("onShowPress");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mFirebaseTextView.setText("onSingleTapUp");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mFirebaseTextView.setText("onScrol");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        mFirebaseTextView.setText("onLongPress");

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mFirebaseTextView.setText("onFling");
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    // End Gesture

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        auth.signOut();
        if(auth.getCurrentUser() == null)
        {
            Log.d(TAG, "LoggedOut");
            startActivity(new Intent(MainActivity.this, SignIn.class));
            finish();
        }
    }
}
