package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.HangmanGameAsyncTask;
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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_request_game);

        context = this;
        hintEdit = (EditText) findViewById(R.id.hint_edit);
        wordEdit = (EditText) findViewById(R.id.word_edit);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent ");
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
        Boolean isDenied = bundle.getBoolean("isDenied");
        Log.i(TAG, "isDenied: " + isDenied);
        if (isDenied)
            showDeniedDialog();
        }

    }

    private void showDeniedDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RequestGameActivity.this);
        dialogBuilder.setMessage(context.getString(R.string.pepper_denied_the_request));
        dialogBuilder.setPositiveButton(context.getString(R.string.request), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendBtn.setEnabled(true);
            }
        });

        dialogBuilder.setNegativeButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(RequestGameActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                sendHintAndWordToServer(hintEdit.getText().toString(), wordEdit.getText().toString());
                break;
        }
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

        HangmanGameAsyncTask hangmanGameAsyncTask = new HangmanGameAsyncTask(this, "startgame", new StringResponseCallback() {
            @Override
            public void onResponseReceived(String result) {
                if(result.equals(context.getString(R.string.succeed)))
                    sendBtn.setEnabled(false);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        });
        hangmanGameAsyncTask.execute(jsonData);
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
        if(sendBtn.isEnabled())
            dialogBuilder.setMessage(context.getString(R.string.do_you_want_to_go_back));
        else
            dialogBuilder.setMessage(context.getString(R.string.you_already_sent_game_request_to_pepper));
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(RequestGameActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
