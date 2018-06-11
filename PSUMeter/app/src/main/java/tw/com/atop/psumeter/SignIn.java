package tw.com.atop.psumeter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {
    private Button signin_btn;
    private EditText username_txt, password_txt;
    private FirebaseAuth auth;
    private static final String TAG = "DB0x02";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //View
        signin_btn = (Button) findViewById(R.id.signin_btn);
        username_txt = (EditText) findViewById(R.id.username_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);

        signin_btn.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "SignInClicked");
                        loginUser(username_txt.getText().toString(), password_txt.getText().toString());
                    }
                }
        );

        //Init Firebase Auth
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null)
        {
            startActivity(new Intent(SignIn.this,  MainActivity.class));
            finish();
        }

    }

    private void loginUser(String username, final String password) {
        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            if (password.length() < 6)
                            {
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                                toast.makeText(SignIn.this, "Password is too short", toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                                toast.makeText(SignIn.this, "Check your email and password", toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            String UID = auth.getCurrentUser().getUid();
                            Log.d(TAG, UID);
                            if(UID.equals("vRJF8sBlo9NDaithuAXthbxWbcA3"))
                            {
                                startActivity(new Intent (SignIn.this, MainActivity.class));
                                finish();
                            }
                            else
                            {
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                                toast.makeText(SignIn.this, "You don't have permission", toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }
}
