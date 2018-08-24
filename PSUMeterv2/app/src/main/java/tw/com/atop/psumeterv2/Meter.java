package tw.com.atop.psumeterv2;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.anastr.speedviewlib.SpeedView;

public class Meter extends AppCompatActivity {

    private TextView back, viewID;
    private FirebaseAuth auth;
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    private ArrayList<String> dataLists;
    private ListView mydata;
    private String UID;
    private String MID;
    private String TAG =  "DB0x10";
    private SpeedView speedView, p0View, p1View, p2View;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter);
        //View
        back = (TextView) findViewById(R.id.back_meter);
        viewID = (TextView) findViewById(R.id.viewID);
        mydata = (ListView) findViewById(R.id.meter_param_list);
        speedView = (SpeedView) findViewById(R.id.speedView);
        p0View = (SpeedView)findViewById(R.id.PA_meter);
        p1View = (SpeedView)findViewById(R.id.PB_meter);
        p2View = (SpeedView)findViewById(R.id.PC_meter);


        //get intent
        Intent intent = getIntent();
        MID = intent.getStringExtra("MID").split(":")[1];
        Log.d(TAG, "MeterID= " + MID);
        viewID.setText("Meter ID: " + MID);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        //Init list
        dataLists = new ArrayList<>();
        //Session check
        if(auth.getCurrentUser() != null)
        {
            // Get firebase database reference
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            UID = auth.getCurrentUser().getUid();
            Log.d(TAG, UID);
            if(UID.equals("vRJF8sBlo9NDaithuAXthbxWbcA3"))
            {
                Log.d(TAG, "Admin");
                UID = "";
            }
        }
        else
        {
            Log.d(TAG, "CurrentUserIsNull");
            startActivity(new Intent(Meter.this, MainActivity.class));
            finish();
        }

        final ValueEventListener valueEventListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataLists.clear();

                Map power = (Map) dataSnapshot.child(UID + "/" + MID + "/power").getValue();
                String p0 = String.valueOf(power.get("p0"));
                String p1 = String.valueOf(power.get("p1"));
                String p2 = String.valueOf(power.get("p2"));
                //Log.d(TAG, "PA: " + p0 + " W");
                //Log.d(TAG, "PB: " + p1 + " W");
                //Log.d(TAG, "PC: " + p2 + " W");
                Log.d(TAG, power.toString());
                dataLists.add("PA: " + p0 + " W");
                dataLists.add("PB: " + p1 + " W");
                dataLists.add("PC: " + p2 + " W");

                Map energy = (Map) dataSnapshot.child(UID + "/" + MID + "/energy").getValue();
                String e0 = String.valueOf(energy.get("tp0"));
                String e1 = String.valueOf(energy.get("tp1"));
                String e2 = String.valueOf(energy.get("tp2"));
                String et = String.valueOf(energy.get("total"));
                double e0_num = Double.valueOf(e0);
                double e1_num = Double.valueOf(e1);
                double e2_num = Double.valueOf(e2);
                double et_num = Double.valueOf(et);
                e0_num = Double.parseDouble(String.format("%.2f", e0_num));
                e1_num = Double.parseDouble(String.format("%.2f", e1_num));
                e2_num = Double.parseDouble(String.format("%.2f", e2_num));
                et_num = Double.parseDouble(String.format("%.2f", et_num));
                dataLists.add("EA: " + String.valueOf(e0_num) + " kWh");
                dataLists.add("EB: " + String.valueOf(e1_num) + " kWh");
                dataLists.add("EC: " + String.valueOf(e2_num) + " kWh");
                dataLists.add("ET: " + String.valueOf(et_num) + " kWh");

                Log.d(TAG, UID + "/Peak");
                Map peak = (Map) dataSnapshot.child(UID + "/Peak").getValue();
                //Log.d(TAG, peak.toString());
                String peak0 = String.valueOf(peak.get(MID + "_PA"));
                String peak1 = String.valueOf(peak.get(MID + "_PB"));
                String peak2 = String.valueOf(peak.get(MID + "_PC"));
                String pavg = String.valueOf(peak.get(MID + "_Pavg"));

                dataLists.add("PeakA: " + peak0 + "W");
                dataLists.add("PeakB: " + peak1 + "W");
                dataLists.add("PeakC: " + peak2 + "W");

                float f_pavg = Float.parseFloat(pavg);
                float f_p0 = Float.parseFloat(p0);
                float f_p1 = Float.parseFloat(p1);
                float f_p2 = Float.parseFloat(p2);
                float f_total = f_p0 + f_p1 + f_p2;
                String ptotal = String.valueOf(f_total);
                dataLists.add("Ptotal: " + ptotal + "W");
                dataLists.add("Pavg: " + pavg + "W");
                /*
                if(f_pavg > 1000)
                {
                    speedView.setUnit("kW");
                    speedView.setMaxSpeed(30);
                    f_pavg = f_pavg/1000;
                }
                else
                {
                    speedView.setUnit("W");
                    speedView.setMaxSpeed(1000);
                }
                //End Pavg
                */
                if(f_total > 1000)
                {
                    speedView.setUnit("kW");
                    speedView.setMaxSpeed(100);
                    f_total = f_total/1000;
                }
                else
                {
                    speedView.setUnit("W");
                    speedView.setMaxSpeed(1000);
                }

                if(f_p0 > 1000)
                {
                    p0View.setUnit("kW");
                    p0View.setMaxSpeed(30);
                    f_p0 = f_p0/1000;
                }
                else
                {
                    p0View.setUnit("W");
                    p0View.setMaxSpeed(1000);
                }
                //End P0

                if(f_p1 > 1000)
                {
                    p1View.setUnit("kW");
                    p1View.setMaxSpeed(30);
                    f_p1 = f_p1/1000;
                }
                else
                {
                    p1View.setUnit("W");
                    p1View.setMaxSpeed(1000);
                }
                //End P1

                if(f_p2 > 1000)
                {
                    p2View.setUnit("kW");
                    p2View.setMaxSpeed(30);
                    f_p2 = f_p2/1000;
                }
                else
                {
                    p2View.setUnit("W");
                    p2View.setMaxSpeed(1000);
                }
                //End P2

                speedView.speedTo(f_total);
                p0View.speedTo(f_p0);
                p1View.speedTo(f_p1);
                p2View.speedTo(f_p2);

                mydata.invalidateViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLists);
        mydata.setAdapter(adapter);


        back.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(Meter.this, Dashboard.class));
                        //Stop listening
                        myRef.removeEventListener(valueEventListener);
                        finish();
                    }
                }
        );

    }
}
