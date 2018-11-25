package theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import theshakers.cmpt276.sfu.ca.robottelepresense.App;
import theshakers.cmpt276.sfu.ca.robottelepresense.R;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.StringResponseCallback;

/**
 * Created by baesubin on 2018-11-04.
 */

// This is AsyncTask used to send and receive Json data from Flask Server
public class SendAndReceiveJsonAsyncTask extends AsyncTask<String, Void, String> {
    private final String TAG = "SendAndReceiveJsonAT";
    private HttpURLConnection conn = null;
    private String returnMsg = "";
    private Context context;

    private StringResponseCallback stringResponseCallback = null;

    public SendAndReceiveJsonAsyncTask(Context context, StringResponseCallback stringResponseCallback) {
        this.stringResponseCallback = stringResponseCallback;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(App.httpAddress + "message");
            //URL url = new URL("http://142.58.170.104:5000/echo");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);

            JSONObject jsonData = new JSONObject();

            jsonData.put("message", params[0]);
            jsonData.put("username", sharedPreferences.getString("username", ""));
            jsonData.put("ASK", sharedPreferences.getString("ASK", ""));
            jsonData.put("pep_id", sharedPreferences.getString("selected_pepper_id", ""));

            Log.i(TAG, "username: " + sharedPreferences.getString("username", ""));
            Log.i(TAG, "ASK: " + sharedPreferences.getString("ASK", ""));
            Log.i(TAG, "pep_id: " + sharedPreferences.getString("selected_pepper_id", ""));

            Log.i(TAG, "sent message: " + params[0]);
            byte[] buf = jsonData.toString().getBytes();

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonData.toString());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                returnMsg = jsonObject.getString("msg");
                Log.i(TAG, "Parsed to JSON: " + jsonObject.getString("msg"));
            } catch (Throwable tx) {
                Log.i(TAG, "Could not parse malformed JSON: " + response.toString());
            }

            os.flush();
            os.close();
        } catch (Exception e) {
            returnMsg = context.getResources().getString(R.string.error_connection);
            Log.e(TAG, "SocketException, " + e);
        } finally {
            conn.disconnect();
        }
        return returnMsg;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "result:  " + result);
        if(result.equals("")) {
            result = context.getResources().getString(R.string.error_wrong_attempt);
        }
        stringResponseCallback.onResponseReceived(result);
    }
}
