package tw.com.atop.atoplink;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
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

public class MainActivity extends AppCompatActivity{
    //https://www.youtube.com/watch?v=9ARoMRd1kXo
    Button signin_btn, signup_btn;
    EditText username_txt, password_txt;
    TextView forgot_password_lbl;


    private FirebaseAuth auth;
    private static final String TAG = "DB0x01";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //View
        signin_btn = (Button) findViewById(R.id.signin_btn);
        username_txt = (EditText) findViewById(R.id.username_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);
        signup_btn = (Button) findViewById(R.id.signup_btn);
        forgot_password_lbl = (TextView) findViewById(R.id.forgot_password_lbl);
        //activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        Log.d(TAG, "BeforeCallOnClick");

        forgot_password_lbl.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "ForgotPassClicked");
                        startActivity(new Intent(MainActivity.this, ForgotPassword.class));
                        finish();
                    }
                }
        );
        signup_btn.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "SignUpClicked");
                        startActivity(new Intent(MainActivity.this, SignUp.class));
                        finish();
                    }
                }
        );
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
            startActivity(new Intent (MainActivity.this, Dashboard.class));
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
                                toast.makeText(MainActivity.this, "Password is too short", toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                                toast.makeText(MainActivity.this, "Check your email and password", toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            startActivity(new Intent (MainActivity.this, Dashboard.class));
                            finish();
                        }
                    }
                });
    }

}
