package tw.com.atop.atoplink;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class Dashboard extends AppCompatActivity {

    private TextView user, change_password_lbl;
    private Button logout_btn, test_btn;
    private static final String TAG = "DB0x03";
    private FirebaseAuth auth;
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    private ArrayList<String> dataLists;
    private ListView mydata;


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

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        //Init list
        dataLists = new ArrayList<>();

        //Session check
        if(auth.getCurrentUser() != null)
        {
            user.setText(auth.getCurrentUser().getEmail());
            // Get firebase database reference
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            Log.d(TAG, auth.getCurrentUser().getUid());
        }
        else
        {
            Log.d(TAG, "CurrentUserIsNull");
            startActivity(new Intent(Dashboard.this, MainActivity.class));
            finish();
        }


        change_password_lbl.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "ChangePasswordClicked");
                        startActivity(new Intent(Dashboard.this, SignIn.class));
                        finish();
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
                    }
                }
        );
        ValueEventListener mymeter = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataLists.clear();
                String UID = auth.getCurrentUser().getUid();
                //MjxY653KiDM6egJOtUGtvhDW0iJ2
                Map map = (Map) dataSnapshot.getValue();
                String data = String.valueOf(map.get("debug"));
                Log.d(TAG, data);

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


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
            finish();return true;
            case R.id.output:startActivity(new Intent(Dashboard.this, Output.class));
            finish();return true;
            case R.id.about:startActivity(new Intent(Dashboard.this, About.class));
            finish();return true;
            default:return true;

        }

    }
}
