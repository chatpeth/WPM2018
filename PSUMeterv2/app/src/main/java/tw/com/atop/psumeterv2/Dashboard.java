package tw.com.atop.psumeterv2;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class Dashboard extends AppCompatActivity {

    private TextView user, change_password_lbl;
    private Button logout_btn, test_btn;
    private static final String TAG = "DB0x03";
    private FirebaseAuth auth;
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    private ArrayList<String> dataLists, meterList;
    private ListView mydata, my_meter_list;
    public String clickedMeter;
    private String UID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //view
        user = (TextView) findViewById(R.id.user_lbl);
        change_password_lbl = (TextView) findViewById(R.id.change_password_lbl);
        logout_btn = (Button) findViewById(R.id.logout_btn);
        test_btn = (Button) findViewById(R.id.test_btn);
        mydata = (ListView)findViewById(R.id.all_data_list_main);
        my_meter_list = (ListView) findViewById(R.id.my_meter_list);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        //Init list
        dataLists = new ArrayList<>();
        meterList = new ArrayList<>();

        //Session check
        if(auth.getCurrentUser() != null)
        {
            Log.d(TAG, "GetUID");
            user.setText(auth.getCurrentUser().getEmail());
            // Get firebase database reference
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            //vRJF8sBlo9NDaithuAXthbxWbcA3 --> Admin
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
            startActivity(new Intent(Dashboard.this, MainActivity.class));
            finish();
        }


        final ValueEventListener mymeter = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dataLists.clear();
                    //MjxY653KiDM6egJOtUGtvhDW0iJ2
                    Map map = (Map) dataSnapshot.getValue();
                    //String data = String.valueOf(map.get("debug"));
                    //Log.d(TAG, data);

                    //Read All data
                    Map path = (Map) dataSnapshot.child(UID + "/path").getValue();
                    Log.d(TAG, path.toString());
                    String path_num = String.valueOf(path.get("path_num"));
                    Log.d(TAG, "path_num= " + path_num);


                    for(int i = 0; i < Integer.parseInt(path_num); i++)
                    {
                        String tmp_path = String.valueOf(path.get("path" + String.valueOf(i + 1)));
                        Log.d("NullTag", "tmp_path= " + tmp_path);
                        Log.d(TAG, "tmp_path= " + tmp_path);
                        if(!tmp_path.equals("null"))
                        {

                            String[] splitPath = new String[0];
                            try {
                                splitPath = tmp_path.split("@");
                            } catch (Exception e) {
                                Log.d(TAG, "splitError");
                                e.printStackTrace();
                                break;
                            }
                            Log.d(TAG, "path= " + splitPath[0]);
                            Log.d(TAG, "data_name= " + splitPath[1]);
                            Log.d(TAG, "full_path= " + UID + "/" + splitPath[0]);
                            Map tmp_data = (Map) dataSnapshot.child(UID + "/" + splitPath[0]).getValue();
                            String data_str = String.valueOf(tmp_data.get(splitPath[1]));
                            Log.d(TAG, "value= " + data_str);
                            dataLists.add(tmp_path + ": " + data_str);
                        }
                        else
                        {
                            Log.d("NullTag", "RemovedVar");
                        }
                    }
                    mydata.invalidateViews();


                    //Get power
                /*
                Map power = (Map) dataSnapshot.child(UID + "/1/power").getValue();
                String p0 = String.valueOf(power.get("p0"));
                String p1 = String.valueOf(power.get("p1"));
                String p2 = String.valueOf(power.get("p2"));

                Log.d(TAG, "PA: " + p0 + " W");
                Log.d(TAG, "PB: " + p1 + " W");
                Log.d(TAG, "PC: " + p2 + " W");
                Log.d(TAG, power.toString());
                */
                } catch (Exception e) {
                    e.printStackTrace();

                }


                try {
                    meterList.clear();
                    //Read All data
                    Map path = (Map) dataSnapshot.child(UID + "/meter").getValue();
                    Log.d(TAG, path.toString());
                    String path_num = String.valueOf(path.get("meter_num"));
                    Log.d(TAG, "meter_num= " + path_num);
                    for(int i = 0; i < Integer.parseInt(path_num); i++){
                        String tmp_path = String.valueOf(path.get("meter" + String.valueOf(i + 1)));
                        Log.d("NullTag", "tmp_path= " + tmp_path);
                        Log.d(TAG, "tmp_path= " + tmp_path);
                        if(!tmp_path.equals("null"))
                        {
                            Log.d(TAG, "Meter: " + tmp_path);

                            Map energy = (Map) dataSnapshot.child(UID + "/" + tmp_path + "/energy").getValue();
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
                            meterList.add("Meter:" + tmp_path + ": " + String.valueOf(et_num) + " kWh");

                        }
                        else
                        {
                            Log.d(TAG, "NullVar");
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                my_meter_list.invalidateViews();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        logout_btn.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "LogoutClicked");
                        logoutUser();
                        myRef.removeEventListener(mymeter);
                    }
                }
        );

        change_password_lbl.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "ChangePasswordClicked");
                        myRef.removeEventListener(mymeter);
                        startActivity(new Intent(Dashboard.this, SignIn.class));
                        finish();
                    }
                }
        );

        test_btn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View view){
                        Map<String, Object> value = new HashMap<String, Object>();
                        value.put("debug", "DB0x01");
                        myRef.updateChildren(value);

                    }
                }
        );
        ArrayAdapter<String > adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLists);
        mydata.setAdapter(adapter);

        //Meter ListView
        ArrayAdapter<String > meter_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, meterList);
        my_meter_list.setAdapter(meter_adapter);

        my_meter_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myRef.removeEventListener(mymeter);
                clickedMeter = parent.getItemAtPosition(position).toString();
                Log.d(TAG,  clickedMeter + " Clicked");
                //startActivity(new Intent(Dashboard.this, Meter.class));
                Intent intent = new Intent(Dashboard.this, Meter.class);
                intent.putExtra("MID", clickedMeter);
                startActivity(intent);
                finish();

            }
        });
    }

    private void logoutUser() {
        auth.signOut();
        if(auth.getCurrentUser() == null)
        {
            Log.d(TAG, "LoggedOut");
            startActivity(new Intent(Dashboard.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.edit:startActivity(new Intent(Dashboard.this, Edit.class));
                break;
            case R.id.output:startActivity(new Intent(Dashboard.this, Output.class));
                break;
            case R.id.about:startActivity(new Intent(Dashboard.this, About.class));
                break;
            case R.id.deviceManager:startActivity(new Intent(Dashboard.this, DeviceManager.class));
                break;
            default:break;

        }
        return true;

    }


}

