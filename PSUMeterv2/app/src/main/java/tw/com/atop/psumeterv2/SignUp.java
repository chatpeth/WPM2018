package tw.com.atop.psumeterv2;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity{

    Button register_btn;
    TextView exist_user_lbl;
    EditText signup_username_txt, signup_password_txt;
    private String UID;
    public DatabaseReference myRef;
    public FirebaseDatabase database;

    private FirebaseAuth auth;
    private static final String TAG = "DB0x02";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //View
        register_btn = (Button) findViewById(R.id.Register_btn);
        exist_user_lbl = (TextView) findViewById(R.id.exist_user_lbl);
        signup_username_txt = (EditText) findViewById(R.id.signup_username_txt);
        signup_password_txt = (EditText) findViewById(R.id.signup_password_txt);

        register_btn.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "RegisterClicked");
                        signUpUser(signup_username_txt.getText().toString(), signup_password_txt.getText().toString());
                        //startActivity(new Intent(SignUp.this, MainActivity.class));
                        //finish();
                    }
                }
        );
        exist_user_lbl.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "ExistUserClicked");
                        startActivity(new Intent(SignUp.this, MainActivity.class));
                        finish();
                    }
                }
        );


        //Init Firebase
        auth = FirebaseAuth.getInstance();
    }


    private void signUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                            toast.makeText(SignUp.this, "Error: " + task.getException(), toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                            toast.makeText(SignUp.this, "Registered", toast.LENGTH_SHORT).show();
                            UID  = auth.getCurrentUser().getUid();
                            Log.d(TAG, "Registered");
                            // Get firebase database reference
                            database = FirebaseDatabase.getInstance();
                            myRef = database.getReference();
                            Map<String, Object> value = new HashMap<String, Object>();
                            value.put(UID + "/path/path_num", 0);
                            myRef.updateChildren(value);

                            value.put(UID + "/DO/do_num", 0);
                            myRef.updateChildren(value);

                            value.put(UID + "/default", 0);
                            myRef.updateChildren(value);

                            value.put(UID + "/meter/meter_num", 0);
                            myRef.updateChildren(value);

                            Log.d(TAG, "DatabaseCreated");

                        }
                    }
                });

    }
}
