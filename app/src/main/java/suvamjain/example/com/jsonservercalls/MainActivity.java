package suvamjain.example.com.jsonservercalls;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView mac;
    EditText reg_no;
    Dialog myDialog;
    FloatingActionButton fab;
    String out;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(MainActivity.this);

        mac   =  (TextView)findViewById(R.id.mac);
        fab = (FloatingActionButton) findViewById(R.id.fab);
//        output   =  (TextView)findViewById(R.id.opt);
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

                    //Toast.makeText(MainActivity.this,"Please enter the last 4 digits of your Registeration Number",Toast.LENGTH_SHORT).show();
                    Snackbar.make(v,"Please enter the last 4 digits of your Reg No.",Snackbar.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }

    public void animate(View v) {

        TextView txtclose,output;
        myDialog.setContentView(R.layout.custompopup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        output =(TextView)myDialog.findViewById(R.id.opt);
        output.setText(out);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                fab.clearAnimation();
            }
        });
        fab.clearAnimation();
        myDialog.show();

    }

    public void reset(View view) {
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reg_no.setText("");
                fab.setVisibility(v.GONE);
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
//            output.setText("");
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

            final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);

            // Use bounce interpolator with amplitude 0.2 and frequency 20
            MyBounceInterpolator interpolator = new MyBounceInterpolator(0.5, 50);
            myAnim.setInterpolator(interpolator);
            fab.setVisibility(View.VISIBLE);
            fab.startAnimation(myAnim);


            if (Error != null) {

               //output.setText("Error is : " + Error);
                out = "Error is : " + Error;

            } else {

                //output.setText("" + SetServerString);
                out = "" + SetServerString;

            }
        }
    }
}
