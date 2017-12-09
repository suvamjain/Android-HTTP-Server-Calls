package suvamjain.example.com.jsonservercalls;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView mac,output;
    EditText reg_no;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mac   =  (TextView)findViewById(R.id.mac);
        output   =  (TextView)findViewById(R.id.opt);
        reg_no =  (EditText)findViewById(R.id.regno);

        Button get =(Button)findViewById(R.id.get);

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        mac.setText(info.getMacAddress());
        // to provide greater data protection, Android 6.0 onwards getMacAddress() always returns a constant value of 02:00:00:00:00:00 if no error found.


        get.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v)
            {
                String regValue    = (reg_no.getText().toString());
                //int regValue=Integer.parseInt(regs);
                if(regValue.length() == 4) {
                    String macValue = (mac.getText().toString());

                    // Create URL string
                    String URLs = "https://android-club-project.herokuapp.com/upload_details?reg_no=" + regValue + "&mac=" + macValue;

                    new GetServerResponse().execute(URLs);
                }
                else{

                    Toast.makeText(MainActivity.this,"Please enter the last 4 digits of your Registeration Number",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }

    public class GetServerResponse extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String SetServerString;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            // NOTE: You can call UI Element here.
            //UI Element
            output.setText("");
            Dialog.setMessage("Connecting to Server...");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
                try
                {
                    // NOTE: Don't call UI Element here.

                    // Create Request to server and get response
                    HttpGet httpget = new HttpGet(urls[0]);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    SetServerString = Client.execute(httpget, responseHandler);
                }
                catch (ClientProtocolException e) {
                    Error = e.getMessage();
                    cancel(true);
                } catch (IOException e) {
                    Error = e.getMessage();
                    cancel(true);
                }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                output.setText("Error is : " + Error);

            } else {

                output.setText("" + SetServerString);

            }
        }
    }
}
