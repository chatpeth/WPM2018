package tw.com.atop.atoplink;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    private TextView current_username, current_password, confirm_password, back;
    private Button change_confirm_btn;
    private static final String TAG = "DB0x04";
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //View
        current_username = (TextView) findViewById(R.id.current_username);
        current_password = (TextView) findViewById(R.id.current_password);
        change_confirm_btn = (Button) findViewById(R.id.change_confirm_btn);
        confirm_password = (TextView) findViewById(R.id.confirm_new_password);
        back = (TextView) findViewById(R.id.back_forgot);

        auth = FirebaseAuth.getInstance();

        back.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "BackClicked");
                        startActivity(new Intent(SignIn.this, MainActivity.class));
                        finish();
                    }
                }
        );

        change_confirm_btn.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "ConfirmChangeClicked");
                        String password1 = confirm_password.getText().toString();
                        String password2 = current_password.getText().toString();
                        Log.d(TAG, "Newpassword: " + password1);
                        if(password1.equals(password2))
                        {
                            changePassword(password1);
                        }
                        else
                        {
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                            toast.makeText(SignIn.this, "Password not match", toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void changePassword(String newPassword) {
        if(auth.getCurrentUser() != null)
        {
            Log.d(TAG, "ChangingPassword");
            FirebaseUser currentUser = auth.getCurrentUser();
            currentUser.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                        toast.makeText(SignIn.this, "Password changed", toast.LENGTH_SHORT).show();
                        Log.d(TAG, "PasswordChanged");
                        startActivity(new Intent(SignIn.this, MainActivity.class));
                        finish();
                    }
                    else
                    {
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
                        toast.makeText(SignIn.this, "Try again later", toast.LENGTH_SHORT).show();
                        Log.d(TAG, "ChangePasswordFailed");
                        startActivity(new Intent(SignIn.this, MainActivity.class));
                        finish();
                    }
                }
            });
        }
    }
}
