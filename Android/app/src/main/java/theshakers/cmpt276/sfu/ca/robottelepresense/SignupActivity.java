package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.StringResponseCallback;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.requestUserAndAuthAsyncTask;

// This Activity allows user to make new account
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private Context context = null;
    private ProgressDialog progressDialog;

    @BindView(R.id.input_user_name) EditText usernameText;
    @BindView(R.id.input_first_name) EditText firstnameText;
    @BindView(R.id.input_last_name) EditText lastnameText;
    @BindView(R.id.input_email) EditText emailText;
    @BindView(R.id.input_password) EditText passwordText;
    @BindView(R.id.input_reEnterPassword) EditText reEnterPasswordText;
    @BindView(R.id.btn_signup) Button signupBtn;
    @BindView(R.id.link_login) TextView loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signup);
        context = this;

        ButterKnife.bind(this);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupBtn.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        String name = usernameText.getText().toString();
        String firstName = firstnameText.getText().toString();
        String lastName = lastnameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        sendSignUpRequest(name, firstName, lastName, email, password);
    }


    private void sendSignUpRequest(String userName, String firstName, String lastName, String email, String password) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("username", userName);
            jsonData.put("name", firstName+ " "+lastName);
            jsonData.put("email", email);
            jsonData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestUserAndAuthAsyncTask requestUserAndAuthAsyncTask = new requestUserAndAuthAsyncTask(this, "addUser", new StringResponseCallback() {
            @Override
            public void onResponseReceived(String result) {
                if(result=="OK"){
                    Toast.makeText(getApplicationContext(), context.getString(R.string.account_is_created), Toast.LENGTH_SHORT).show();
                    onSignupSuccess();
                } else {
                    progressDialog.cancel();
                    onSignupFailed();
                }
            }
        });
        requestUserAndAuthAsyncTask.execute(jsonData);
    }

    public void onSignupSuccess() {
        signupBtn.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), context.getString(R.string.signup_failed), Toast.LENGTH_LONG).show();
        signupBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String userName = usernameText.getText().toString();
        String firstName = firstnameText.getText().toString();
        String lastName = lastnameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        if (userName.isEmpty() || userName.length() < 3) {
            usernameText.setError(context.getString(R.string.at_least_3_characters));
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if (firstName.isEmpty() || firstName.length() < 3) {
            firstnameText.setError(context.getString(R.string.at_least_3_characters));
            valid = false;
        } else {
            firstnameText.setError(null);
        }

        if (lastName.isEmpty() || lastName.length() < 3) {
            lastnameText.setError(context.getString(R.string.at_least_3_characters));
            valid = false;
        } else {
            lastnameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(context.getString(R.string.enter_a_valid_username));
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(context.getString(R.string.between_4_and_10_alphanumeric_characters));
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError(context.getString(R.string.password_do_not_match));
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignupActivity.this);
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