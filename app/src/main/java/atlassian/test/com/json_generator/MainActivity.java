package atlassian.test.com.json_generator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import atlassian.test.com.listener.ParserListener;

public class MainActivity extends AppCompatActivity implements ParserListener {

    private EditText txtMessage;
    private TextView displayMessage;
    private Button btnParse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMessage = (EditText)findViewById(R.id.message);
        displayMessage = (TextView) findViewById(R.id.jsonOutput);
        btnParse = (Button) findViewById(R.id.btnParse);

        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Parser(txtMessage.getText().toString(),MainActivity.this).generateJsonString();
                txtMessage.setText("");
            }
        });

    }

    @Override
    public void onReceiveJsonString(String jsonString) {

        displayMessage.setText(jsonString.replace("\\",""));

    }
}
