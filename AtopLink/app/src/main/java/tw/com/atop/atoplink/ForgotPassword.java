package tw.com.atop.atoplink;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private Button reset_password_btn;
    private TextView find_email, back;
    private static final String TAG = "DB0x05";

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //View
        reset_password_btn = (Button) findViewById(R.id.reset_password_btn);
        back = (TextView) findViewById(R.id.back_resetPass);
        find_email = (EditText) findViewById(R.id.find_user_txt);

        auth = FirebaseAuth.getInstance();

        reset_password_btn.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "ResetPasswordClicked");
                        resetPassword(find_email.getText().toString());

                    }
                }
        );

        back.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "BackClicked");
                        startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                        finish();
                    }
                }
        );
    }

    private void resetPassword(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                            toast.makeText(ForgotPassword.this, "Check reset password link on your email", toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                            toast.makeText(ForgotPassword.this, "Failed, try again later", toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
