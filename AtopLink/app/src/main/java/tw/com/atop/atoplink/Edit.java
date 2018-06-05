package tw.com.atop.atoplink;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class Edit extends AppCompatActivity {
    private Button add_data_btn, remove_data_btn;
    private EditText path_txt, data_txt;
    private TextView back, clear_data, key;
    private static final String TAG = "DB0x06";
    private FirebaseAuth auth;
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    private String UID;
    private String path_num;
    private Map map;
    private ArrayList<String> dataLists;
    private ListView mydata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //View
        add_data_btn = (Button) findViewById(R.id.add_data_btn);
        remove_data_btn = (Button) findViewById(R.id.remove_data_btn);
        path_txt = (EditText) findViewById(R.id.path_txt);
        data_txt = (EditText) findViewById(R.id.data_txt);
        back = (TextView) findViewById(R.id.back_edit);
        mydata = (ListView) findViewById(R.id.dataLists);
        clear_data = (TextView) findViewById(R.id.clear_data);
        key = (TextView) findViewById(R.id.key_txt);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        //Init list
        dataLists = new ArrayList<>();
        //point to path_txt
        path_txt.requestFocus();

        //Session check
        if(auth.getCurrentUser() != null)
        {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            UID = auth.getCurrentUser().getUid();
            Log.d(TAG, UID);
            key.setText("Key: " + UID);
        }
        else
        {
            Log.d(TAG, "CurrentUserIsNull");
            startActivity(new Intent(Edit.this, MainActivity.class));
            finish();
        }

        add_data_btn.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "AddDataClicked");
                        Map<String, Object> value = new HashMap<String, Object>();
                        dataLists.clear();


                        value.put("debug", "DB0x06");
                        myRef.updateChildren(value);

                        String path = path_txt.getText().toString();
                        String data_name = data_txt.getText().toString();
                        String tmp_path = path + "@" + data_name;
                        String add_data_path = UID + "/" + path + "/" + data_name;
                        if (Character.isDigit(data_name.charAt(0)))
                        {
                            Log.d(TAG, "ERR_VarName_Start_WITH_DIGIT");
                            data_name = "_" + data_name;
                        }
                        value.put(add_data_path, "0");
                        myRef.updateChildren(value);
                        Log.d(TAG, "path= " + path);
                        Log.d(TAG, "data_name= " + data_name);
                        Log.d(TAG, "tmp_path= " + tmp_path);
                        Log.d(TAG, "add_data_path= " + add_data_path);

                        String write_to_path = UID + "/path/path" + String.valueOf(Integer.parseInt(path_num) + 1);
                        Log.d(TAG, "write_to_path= " + write_to_path);
                        value.put(write_to_path, tmp_path);
                        myRef.updateChildren(value);

                        Log.d(TAG, "updateNumPath= " + Integer.parseInt(path_num) + 1);
                        String update_num_path = UID + "/path/path_num";
                        value.put(update_num_path, Integer.parseInt(path_num) + 1);
                        myRef.updateChildren(value);

                        path_txt.requestFocus();
                        add_data_btn.setEnabled(false);

                    }
                }
        );


        remove_data_btn.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        dataLists.clear();
                        path_num = String.valueOf(map.get("path_num"));
                        Log.d(TAG, "RemoveClicked");
                        String path = path_txt.getText().toString();
                        String data_name = data_txt.getText().toString();
                        String tmp_path = path + "@" + data_name;
                        Map<String, Object> value = new HashMap<String, Object>();

                        Log.d(TAG, "path= " + path);
                        Log.d(TAG, "data_name= " + data_name);
                        Log.d(TAG, "tmp_path= " + tmp_path);

                        String write_to_path = UID + "/path/path" + String.valueOf(Integer.parseInt(path_num) + 1);
                        Log.d(TAG, "write_to_path= " + write_to_path);

                        for(int i = 0; i < Integer.parseInt(path_num); i++)
                        {
                            String check_remove = String.valueOf(map.get("path" + String.valueOf(i + 1)));
                            Log.d(TAG, "checkRemove= " + check_remove);
                            if (check_remove.equals(tmp_path))
                            {
                                Log.d(TAG, "foundRemoveID= path" + String.valueOf(i + 1));
                                String remove_path = UID + "/path/" +"path" + String.valueOf(i + 1);
                                Log.d(TAG, "removePath= " + remove_path);
                                DatabaseReference dbNode = FirebaseDatabase.getInstance().getReference().getRoot().child(remove_path);
                                dbNode.setValue("null");
                                //value.put(UID + "/path/path_num", String.valueOf(Integer.parseInt(path_num) - 1));
                                //myRef.updateChildren(value);
                            }
                        }
                        path_txt.requestFocus();
                        remove_data_btn.setEnabled(false);

                    }

                }
        );


        back.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "backClicked");
                        startActivity(new Intent(Edit.this, MainActivity.class));
                        finish();
                    }
                }
        );

        clear_data.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        dataLists.clear();
                        //Do not forget to confirm!!
                        Log.d(TAG, "ClearDataClicked");
                        Map<String, Object> value = new HashMap<String, Object>();
                        value.put(UID + "/path/path_num", 0);
                        myRef.updateChildren(value);
                        startActivity(new Intent(Edit.this, Edit.class));
                        finish();
                    }
                }
        );


        data_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                add_data_btn.setEnabled(true);
                remove_data_btn.setEnabled(true);

            }
        });

        ValueEventListener valueEventListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataLists.clear();
                map = (Map) dataSnapshot.child(UID + "/path").getValue();
                path_num = String.valueOf(map.get("path_num"));
                Log.d(TAG, "path_num= " + path_num);
                Integer num_of_var = Integer.parseInt(path_num);
                for (int i = 0; i < num_of_var; i++ )
                {
                    String tmp_list_var = String.valueOf(map.get("path" + String.valueOf(i + 1)));
                    tmp_list_var = tmp_list_var.replace("@", "/");
                    Log.d(TAG, "var " + tmp_list_var);
                    if(tmp_list_var.equals("null"))
                    {
                        Log.d("NullTag", "NullVar: path" + String.valueOf(i + 1));
                    }
                    else
                    {
                        dataLists.add(tmp_list_var);
                        Log.d(TAG, "tmp_list_var= " + tmp_list_var);
                    }
                }
                mydata.invalidateViews();


            }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String > adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLists);
        mydata.setAdapter(adapter);

        mydata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = parent.getItemAtPosition(position).toString();
                String clickedPath;
                Log.d(TAG, clickedItem);

                String[] splitText = new String[0];
                try {
                    splitText = clickedItem.split("/");
                    clickedPath = splitText[0];
                    for(int i = 1; i < splitText.length - 1; i++)
                    {
                        clickedPath = clickedPath + "/" +splitText[i];
                    }
                    path_txt.setText(clickedPath);

                    data_txt.setText(splitText[splitText.length - 1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
