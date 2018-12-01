package theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import theshakers.cmpt276.sfu.ca.robottelepresense.App;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.StringResponseCallback;

/**
 * Created by baesubin on 2018-11-04.
 */

// This is AsyncTask used to login request to Cloud Server
public class LoginAsyncTask extends AsyncTask<JSONObject, Void, String> {
    private final String TAG = "LoginAsyncTask";
    private HttpURLConnection conn = null;
    private String returnMsg = "";
    private Context context = null;
    private URL url = null;

    private StringResponseCallback stringResponseCallback = null;

    public LoginAsyncTask(Context context, String path, StringResponseCallback stringResponseCallback) {
        this.stringResponseCallback = stringResponseCallback;
        this.context = context;
        try {
            this.url = new URL(App.httpAddress + path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonData = params[0];
            Log.i(TAG, "sent message: " + params[0]);
            //byte[] buf = jsonData.toString().getBytes();

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(jsonData.toString());

            int status = conn.getResponseCode();
            Log.i(TAG, "conn.getResponseCode(): "+status);
            if (status == 400) {
                returnMsg = "Bad_Request";
            } else if (status == 401) {
                returnMsg = "Unauthorized";
            } else if (status == 409) {
                returnMsg = "Conflict";
            } else if (status == 500) {
                returnMsg = "Internal_Server_Error";
            } else if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                in.close();

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray authorizedPepperJsonArr = jsonObject.getJSONArray("pepper_list");
                    JSONArray requestedPepperJsonArr = jsonObject.getJSONArray("request_list");

                    SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("username", jsonData.getString("username"));
                    edit.putString("email", jsonObject.getString("email"));
                    edit.putString("ASK", jsonObject.getString("ASK"));
                    Log.i(TAG, "ASK: " + jsonObject.getString("ASK"));

                    edit.putString("pepper_list", authorizedPepperJsonArr.toString());
                    edit.putString("request_list", requestedPepperJsonArr.toString());
                    edit.apply();
                    returnMsg = "Succeed";
                } catch (Throwable tx) {
                    Log.i(TAG, "Could not parse malformed JSON: " + response.toString());
                }
            }
            dataOutputStream.close();
            return returnMsg;
        } catch (Exception e) {
            returnMsg = "Exception";
            Log.e(TAG, "Exception, " + e);
        } finally {
            conn.disconnect();
        }
        return returnMsg;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "on PostExecute() result:  " + result);
        stringResponseCallback.onResponseReceived(result);
    }
}
