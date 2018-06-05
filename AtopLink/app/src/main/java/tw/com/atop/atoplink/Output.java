package tw.com.atop.atoplink;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class Output extends AppCompatActivity {
    private TextView back_btn, clear_btn;
    private Spinner spinner, DO_spinner;
    private TextView selectedItem;
    private TextView outputSelected;

    private FirebaseAuth auth;
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    private String UID;

    private ArrayList<String> list;
    private ArrayList<String> digital_output;
    private Button add_to_do, on_btn, off_btn;
    private String do_num;
    private Map map;

    private static final String TAG = "DB0x07";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        Log.d(TAG, "OutputOnCreate");

        //View
        back_btn = (TextView)findViewById(R.id.back_output);
        spinner = (Spinner)findViewById(R.id.output_lists);
        selectedItem = (TextView) findViewById(R.id.selected_output);
        DO_spinner = (Spinner) findViewById(R.id.filterred_output_lists);
        add_to_do = (Button) findViewById(R.id.add_to_do);
        on_btn = (Button) findViewById(R.id.on_btn);
        off_btn = (Button) findViewById(R.id.off_btn);
        outputSelected = (TextView) findViewById(R.id.selected_do);
        clear_btn = (TextView) findViewById(R.id.clear_output);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

        //Init list
        list = new ArrayList<>();
        digital_output = new ArrayList<>();

        //Session check
        if(auth.getCurrentUser() != null)
        {
            // Get firebase database reference
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            UID = auth.getCurrentUser().getUid();
            Log.d(TAG, UID);
        }
        else
        {
            Log.d(TAG, "CurrentUserIsNull");
            startActivity(new Intent(Output.this, MainActivity.class));
            finish();
        }

        ValueEventListener mymeter = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                list.clear();
                list.add("default");
                //Read All data
                Map path = (Map) dataSnapshot.child(UID + "/path").getValue();
                Log.d(TAG, path.toString());
                String path_num = String.valueOf(path.get("path_num"));
                Log.d(TAG, "path_num= " + path_num);
                for(int i = 0; i < Integer.parseInt(path_num); i++){
                    String tmp_path = String.valueOf(path.get("path" + String.valueOf(i + 1)));
                    Log.d(TAG, "tmp_path= " + tmp_path);
                    if(!tmp_path.equals("null")){
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
                        list.add(tmp_path);
                    }
                    else
                    {
                        Log.d("NullTag", "NullOutput");
                    }
                }

                //readNumberOfDo
                map = (Map) dataSnapshot.child(UID + "/DO").getValue();
                do_num = String.valueOf(map.get("do_num"));
                Log.d(TAG, "do_num= " + do_num);
                Integer num_of_var = Integer.parseInt(do_num);

                //Add to digital output spin
                digital_output.clear();
                digital_output.add("default");
                for(int i = 0; i < Integer.parseInt(do_num); i++){
                    String do_path = String.valueOf(map.get("do" + String.valueOf(i + 1)));
                    Log.d(TAG, "do_path= " + do_path);
                    if(!do_path.equals("null")){
                        String[] splitPath = new String[0];
                        try {
                            splitPath = do_path.split("@");
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
                        digital_output.add(do_path);
                    }
                    else
                    {
                        Log.d(TAG, "NullDoPath");
                    }
                }
                Log.d(TAG, "digitalOutputSize= " + String.valueOf(digital_output.size()));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        list.add("default");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(adapter);

        digital_output.add("default");
        ArrayAdapter<String> output_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, digital_output);
        DO_spinner.setAdapter(output_adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem.setText(parent.getItemAtPosition(position).toString());
                Log.d(TAG, "ItemSelected: " + parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        DO_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "OutputSelected: " + parent.getItemAtPosition(position).toString());
                String tmp_output = parent.getItemAtPosition(position).toString();
                tmp_output = tmp_output.replace("@", "/");
                outputSelected.setText(tmp_output);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        add_to_do.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "AddToDoClicked");
                        if(!selectedItem.getText().toString().equals("default"))
                        {
                            Map<String, Object> value = new HashMap<String, Object>();
                            value.put(UID + "/DO/do" + String.valueOf(Integer.parseInt(do_num) + 1), selectedItem.getText().toString());
                            myRef.updateChildren(value);

                            //Update number of DO
                            Log.d(TAG, "updateDoPath= " + Integer.parseInt(do_num) + 1);
                            String update_num_path = UID + "/DO/do_num";
                            value.put(update_num_path, Integer.parseInt(do_num) + 1);
                            myRef.updateChildren(value);

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                            toast.makeText(Output.this, selectedItem.getText().toString() + " added", toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

        back_btn.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Output.this, MainActivity.class));
                        finish();
                    }
                }
        );

        on_btn.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> value = new HashMap<String, Object>();
                        String tmp_path = outputSelected.getText().toString();
                        tmp_path = tmp_path.replace("@", "/");
                        tmp_path = UID + "/" + tmp_path;
                        Log.d(TAG, "On: " + tmp_path);
                        value.put(tmp_path, true);
                        myRef.updateChildren(value);
                    }
                }
        );

        off_btn.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> value = new HashMap<String, Object>();
                        String tmp_path = outputSelected.getText().toString();
                        tmp_path = tmp_path.replace("@", "/");
                        tmp_path = UID + "/" + tmp_path;
                        Log.d(TAG, "Off: " + tmp_path);
                        value.put(tmp_path, false);
                        myRef.updateChildren(value);
                    }
                }
        );


        clear_btn.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "ClearOutputClicked");
                        Map<String, Object> value = new HashMap<>();
                        String update_num_path = UID + "/DO/do_num";
                        value.put(update_num_path, 0);
                        myRef.updateChildren(value);
                    }
                }
        );


    }

}
