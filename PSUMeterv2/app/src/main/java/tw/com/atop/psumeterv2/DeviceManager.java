package tw.com.atop.psumeterv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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

public class DeviceManager extends AppCompatActivity {

    private TextView back;
    private FirebaseAuth auth;
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    private String UID;
    private String meter_num;
    private Map map;
    private ArrayList<String> dataLists;
    private ListView mydata;
    private EditText meter_id;
    private Button add_meter, remove_meter;
    private static final String TAG = "DB0x09";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manager);

        //View
        back = (TextView) findViewById(R.id.back_deviceManager);
        mydata = (ListView) findViewById(R.id.meter_list);
        meter_id = (EditText) findViewById(R.id.meter_id);
        add_meter = (Button) findViewById(R.id.add_meter);
        remove_meter = (Button) findViewById(R.id.remove_meter);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        //Init list
        dataLists = new ArrayList<>();

        //Session check
        if(auth.getCurrentUser() != null)
        {
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
            startActivity(new Intent(DeviceManager.this, MainActivity.class));
            finish();
        }

        add_meter.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String MID = meter_id.getText().toString();
                        Log.d(TAG, "AddClicked");
                        Map<String, Object> value = new HashMap<String, Object>();
                        dataLists.clear();

                        if(!MID.equals(""))
                        {
                            Log.d(TAG, "MeterID: " + MID);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                            toast.makeText(DeviceManager.this, "Meter ID: " + MID + " added", toast.LENGTH_SHORT).show();
                            String tmp_path = UID + "/meter/meter" + String.valueOf(Integer.parseInt(meter_num) + 1);
                            Log.d(TAG, "tmp_path: " + tmp_path);
                            value.put(tmp_path, MID);
                            myRef.updateChildren(value);

                            Log.d(TAG, "updateNumMeter= " + Integer.parseInt(meter_num) + 1);
                            String update_num_path = UID + "/meter/meter_num";
                            value.put(update_num_path, Integer.parseInt(meter_num) + 1);
                            myRef.updateChildren(value);

                            //Energy
                            for(int i = 0; i < 3; i++)
                            {
                                String write_to_path = UID + "/" + MID + "/energy/p" + String.valueOf(i);
                                value.put(write_to_path, 0);
                                myRef.updateChildren(value);
                            }
                            for(int i = 0; i < 3; i++)
                            {
                                String write_to_path = UID + "/" + MID + "/energy/tp" + String.valueOf(i);
                                value.put(write_to_path, 0);
                                myRef.updateChildren(value);
                            }

                            value.put(UID + "/" + MID + "/energy/total", 0);
                            myRef.updateChildren(value);

                            //Power
                            for(int i = 0; i < 3; i++)
                            {
                                String write_to_path = UID + "/" + MID + "/power/p" + String.valueOf(i);
                                value.put(write_to_path, 0);
                                myRef.updateChildren(value);
                            }

                            value.put(UID + "/" + MID + "/setting/log", 1);
                            myRef.updateChildren(value);

                            value.put(UID + "/" + MID + "/setting/log_interval", 5000);
                            myRef.updateChildren(value);

                            value.put(UID + "/" + MID + "/setting/polling_interval", 5000);
                            myRef.updateChildren(value);

                            value.put(UID + "/Peak/" + MID + "_PA", 0);
                            myRef.updateChildren(value);

                            value.put(UID + "/Peak/" + MID + "_PB", 0);
                            myRef.updateChildren(value);

                            value.put(UID + "/Peak/" + MID + "_PC", 0);
                            myRef.updateChildren(value);

                            value.put(UID + "/Peak/" + MID + "_Pavg", 0);
                            myRef.updateChildren(value);

                        }
                        else
                        {
                            Log.d(TAG, "Check your meter ID");
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                            toast.makeText(DeviceManager.this, "Check your meter ID", toast.LENGTH_SHORT).show();
                        }
                        add_meter.setEnabled(false);
                        meter_id.setText("");
                    }
                }
        );

        remove_meter.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "RemoveClicked");
                        String MID = meter_id.getText().toString();
                        Map<String, Object> value = new HashMap<String, Object>();

                        for(int i = 0; i < Integer.parseInt(meter_num); i++)
                        {
                            String check_remove = String.valueOf(map.get("meter" + String.valueOf(i + 1)));
                            Log.d(TAG, "checkRemove= " + check_remove);
                            if (check_remove.equals(MID))
                            {
                                Log.d(TAG, "foundRemoveID= path" + String.valueOf(i + 1));
                                String remove_path = UID + "/meter/meter" + String.valueOf(i + 1);
                                Log.d(TAG, "removePath= " + remove_path);
                                DatabaseReference dbNode = FirebaseDatabase.getInstance().getReference().getRoot().child(remove_path);
                                dbNode.setValue("null");
                                //value.put(UID + "/path/path_num", String.valueOf(Integer.parseInt(path_num) - 1));
                                //myRef.updateChildren(value);
                            }
                        }
                        remove_meter.setEnabled(false);
                        meter_id.setText("");
                    }
                }
        );


        final ValueEventListener valueEventListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataLists.clear();
                map = (Map) dataSnapshot.child(UID + "/meter").getValue();
                meter_num = String.valueOf(map.get("meter_num"));
                Log.d(TAG, "meter_num= " + meter_num);
                Integer num_of_var = Integer.parseInt(meter_num);
                for (int i = 0; i < num_of_var; i++)
                {
                    String tmp_list_var = String.valueOf(map.get("meter" + String.valueOf(i + 1)));
                    Log.d(TAG, "TmpListVar= " + "meter" + String.valueOf(i + 1));
                    if(tmp_list_var.equals("null"))
                    {
                        Log.d(TAG, "NullVar: meter" + String.valueOf(i + 1));
                    }
                    else
                    {
                        dataLists.add("Meter ID: " + tmp_list_var);
                        Log.d(TAG, "tmp_list_var= " + tmp_list_var);
                    }
                }
                mydata.invalidateViews();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        meter_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                add_meter.setEnabled(true);
                remove_meter.setEnabled(true);

            }
        });

        back.setOnClickListener(
                new Button.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "BackClicked");
                        //startActivity(new Intent(DeviceManager.this, MainActivity.class));
                        //Stop listening
                        myRef.removeEventListener(valueEventListener);
                        finish();
                    }
                }
        );

        ArrayAdapter<String > adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLists);
        mydata.setAdapter(adapter);
    }
}
