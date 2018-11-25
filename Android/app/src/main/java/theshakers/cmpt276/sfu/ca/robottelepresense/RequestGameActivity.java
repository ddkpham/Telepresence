package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.LoginAsyncTask;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.RequestGameStartAsyncTask;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.StringResponseCallback;

/**
 * Created by baesubin on 2018-11-07.
 */

// This Activity is for asking hint and word to start Hangman Game with Pepper
public class RequestGameActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "RequestGameActivity";
    private Context context = null;
    private EditText hintEdit = null;
    private EditText wordEdit = null;
    private Button sendBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_game);

        context = this;
        hintEdit = (EditText) findViewById(R.id.hint_edit);
        wordEdit = (EditText) findViewById(R.id.word_edit);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                sendHintAndWordToServer(hintEdit.getText().toString(), wordEdit.getText().toString());
                break;
        }
    }

    private void startActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void sendHintAndWordToServer(String hint, String word) {
        JSONObject jsonData = new JSONObject();
        SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
        try {
            jsonData.put("hint", hint);
            jsonData.put("word", word);
            jsonData.put("android_username", sharedPreferences.getString("username", ""));
            jsonData.put("pep_id", sharedPreferences.getString("selected_pepper_id", ""));
            jsonData.put("FBToken", FirebaseInstanceId.getInstance().getToken());
            Log.i(TAG, "FBToken: "+ FirebaseInstanceId.getInstance().getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestGameStartAsyncTask requestGameStartAsyncTask= new RequestGameStartAsyncTask(this, "startgame", new StringResponseCallback() {
            @Override
            public void onResponseReceived(String result) {
                if(result.equals("OK")) {
                    Toast.makeText(getApplicationContext(), "SENT", Toast.LENGTH_SHORT).show();
                    //startActivity();
                } else if(result.equals("ACCOUNT_ERROR")) {
                    Toast.makeText(getApplicationContext(), "ACCOUNT_ERROR", Toast.LENGTH_SHORT).show();
                    //onLoginFailed(context.getString(R.string.check_your_id_or_password));
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    //onLoginFailed(context.getString(R.string.login_failed));
                }
            }
        });
        requestGameStartAsyncTask.execute(jsonData);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RequestGameActivity.this);
        dialogBuilder.setMessage(context.getString(R.string.do_you_want_to_go_back));
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(RequestGameActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialogBuilder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
