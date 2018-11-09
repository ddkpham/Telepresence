package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.StringResponseCallback;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.requestUserAndAuthAsyncTask;

/**
 * Created by baesubin on 2018-11-07.
 */

// This Activity is for sending pepper authorization
public class RequestAuthoActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "RequestAuthoActivity";
    private EditText pepperEdit = null;
    private Button registerBtn = null;
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_authorization);

        context = this;

        pepperEdit = (EditText)findViewById(R.id.pepperEdit);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(this);
    }


    private void sendAutoRequest() {
        JSONObject jsonData = new JSONObject();
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
            jsonData.put("pep_id", pepperEdit.getText().toString());
            jsonData.put("username", sharedPreferences.getString("username", "defValue"));
            jsonData.put("email", sharedPreferences.getString("email", "defValue"));
            jsonData.put("ASK", sharedPreferences.getString("ASK", ""));
            jsonData.put("PSK", sharedPreferences.getString("PSK", ""));

            Log.i(TAG, "pep_id: " + pepperEdit.getText().toString());
            Log.i(TAG, "username: "+ sharedPreferences.getString("username", "defValue"));
            Log.i(TAG, "email: "+ sharedPreferences.getString("email", "defValue"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestUserAndAuthAsyncTask requestUserAndAuthAsyncTask = new requestUserAndAuthAsyncTask(this, "reqAuth", new StringResponseCallback() {
            @Override
            public void onResponseReceived(String result) {
                if(result=="OK"){
                    Toast.makeText(getApplicationContext(), context.getString(R.string.request_sent), Toast.LENGTH_SHORT).show();

                    try {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
                        JSONArray requestedPepperJsonArray = new JSONArray(sharedPreferences.getString("request_list", "[]"));
                        requestedPepperJsonArray.put(pepperEdit.getText().toString());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("request_list", requestedPepperJsonArray.toString());
                        System.out.println(requestedPepperJsonArray.toString());
                        editor.commit();
                    } catch (Throwable tx) {
                        Log.i(TAG, "Could not parse malformed JSON: " );
                    }

                    startActivity();
                } else {
                    Toast.makeText(getApplicationContext(), context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestUserAndAuthAsyncTask.execute(jsonData);
    }

    private void startActivity() {
        Intent intent = new Intent(this, PepperListActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.registerBtn:
                sendAutoRequest();
                break;
        }
    }
}
