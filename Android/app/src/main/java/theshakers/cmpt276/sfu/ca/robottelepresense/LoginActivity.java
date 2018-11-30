package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.LoginAsyncTask;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.StringResponseCallback;
import theshakers.cmpt276.sfu.ca.robottelepresense.Firebase.MyFirebaseInstanceService;

// LoginActivity allows user to login or access to SignupActivity when user doesn't have account
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private Context context = null;
    private ProgressDialog progressDialog;

    @BindView(R.id.input_name) EditText nameText;
    @BindView(R.id.input_password) EditText passwordText;
    @BindView(R.id.btn_login) Button loginBtn;
    @BindView(R.id.link_signup) TextView signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        MyFirebaseInstanceService.onTokenRefresh();

        context = getApplicationContext();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void sendLoginRequest(String name, String password) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("username", name);
            jsonData.put("password", password);
            jsonData.put("FBToken", FirebaseInstanceId.getInstance().getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoginAsyncTask loginAsyncTask= new LoginAsyncTask(this, "login", new StringResponseCallback() {
            @Override
            public void onResponseReceived(String result) {
                switch (result) {
                    case "Succeed":
                        onLoginSuccess();
                        break;
                    case "Conflict":
                        onLoginFailed(context.getString(R.string.user_does_not_exist));
                        break;
                    case "Bad_Request":
                        onLoginFailed(context.getString(R.string.could_you_check_your_input_data));
                        break;
                    case "Unauthorized":
                        onLoginFailed(context.getString(R.string.username_or_password_does_not_match));
                        break;
                    case "Internal_Server_Error":
                        onLoginFailed(context.getString(R.string.internal_server_error));
                        break;
                    case "Exception":
                        onLoginFailed(context.getString(R.string.exception));
                        break;
                    default:
                        onLoginFailed(context.getString(R.string.error_wrong_attempt));
                        break;
                }
            }
        });
        loginAsyncTask.execute(jsonData);
    }

    public void login() {
        if (!validateInput()) {
            onLoginFailed("");
            return;
        }

        loginBtn.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getString(R.string.authenticating));
        progressDialog.show();

        String name = nameText.getText().toString();
        String password = passwordText.getText().toString();

        sendLoginRequest(name, password);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void onLoginSuccess() {
        Intent intent = new Intent(this, PepperListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        progressDialog.cancel();
        loginBtn.setEnabled(true);
        this.finish();
    }

    public void onLoginFailed(String toastMsg) {
        if(progressDialog!=null)
            progressDialog.cancel();
        if(toastMsg!= "")
            Toast.makeText(getBaseContext(), toastMsg, Toast.LENGTH_LONG).show();
        loginBtn.setEnabled(true);
    }

    public boolean validateInput() {
        boolean isValidInput = true;

        String name = nameText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 1) {
            nameText.setError(context.getString(R.string.enter_a_valid_username));
            isValidInput = false;
        } else {
            nameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(context.getString(R.string.between_4_and_10_alphanumeric_characters));
            isValidInput = false;
        } else {
            passwordText.setError(null);
        }

        return isValidInput;
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage(context.getString(R.string.do_you_really_want_to_exit));
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
