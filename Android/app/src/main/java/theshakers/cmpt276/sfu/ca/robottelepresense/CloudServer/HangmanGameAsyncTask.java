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

import theshakers.cmpt276.sfu.ca.robottelepresense.App;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.StringResponseCallback;
import theshakers.cmpt276.sfu.ca.robottelepresense.R;

/**
 * Created by baesubin on 2018-11-04.
 */

// This is AsyncTask used send HTTP requests and receive to Cloud Server to play Hangman game
public class HangmanGameAsyncTask extends AsyncTask<JSONObject, Void, String> {
    private final String TAG = "HangmanGameAsyncTask";
    private HttpURLConnection conn = null;
    private String returnMsg = "";
    private Context context = null;
    private URL url = null;

    private StringResponseCallback stringResponseCallback = null;

    public HangmanGameAsyncTask(Context context, String path, StringResponseCallback stringResponseCallback) {
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
            byte[] buf = jsonData.toString().getBytes();

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(jsonData.toString());

            int status = conn.getResponseCode();
            if (status == 400) {
                returnMsg = context.getString(R.string.bad_request);
            } else if (status == 406) {
                returnMsg = context.getString(R.string.pepper_does_not_exist);
            } else if (status == 409) {
                returnMsg = context.getString(R.string.user_does_not_exist);
            } else if (status == 410) {
                returnMsg = context.getString(R.string.failed_to_connect_to_pepper);
            } else if (status == 500) {
                returnMsg = context.getString(R.string.internal_server_error);
            } else if(status == 200) {
                returnMsg = context.getString(R.string.succeed);
            }

            dataOutputStream.flush();
            dataOutputStream.close();

            return returnMsg;
        } catch (Exception e) {
            returnMsg =  context.getString(R.string.error_connection);
            Log.e(TAG, "Exception, " + e);
        } finally {
            conn.disconnect();
        }
        return returnMsg;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "on PostExecute() result:  " + result);
        if(result.equals(""))
            result = context.getResources().getString(R.string.error_wrong_attempt);
        stringResponseCallback.onResponseReceived(result);
    }
}
