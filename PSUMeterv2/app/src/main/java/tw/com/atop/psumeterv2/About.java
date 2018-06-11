package tw.com.atop.psumeterv2;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class About extends AppCompatActivity {

    private TextView credit_txt, back;
    private String TAG = "DB0x08";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Log.d(TAG, "AboutOnCreate");

        //View
        credit_txt = (TextView) findViewById(R.id.credit);
        back = (TextView) findViewById(R.id.back_about);

        credit_txt.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "CreditClicked");
                        //https://drive.google.com/open?id=1pFFFJig4chtPPUg22uWt67VbVWJAdwCz
                        //String url = "https://drive.google.com/open?id=1pFFFJig4chtPPUg22uWt67VbVWJAdwCz";
                        //Intent intent = new Intent(Intent.ACTION_VIEW);
                        //intent.setData(Uri.parse(url));
                        //startActivity(intent);
                    }
                }
        );

        back.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Log.d(TAG, "BackClicked");
                        //startActivity(new Intent(About.this, MainActivity.class));
                        finish();

                    }
                }
        );
    }
}
