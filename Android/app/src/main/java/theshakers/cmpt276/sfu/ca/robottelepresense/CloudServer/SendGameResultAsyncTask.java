package theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import theshakers.cmpt276.sfu.ca.robottelepresense.App;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.StringResponseCallback;
import theshakers.cmpt276.sfu.ca.robottelepresense.R;

/**
 * Created by baesubin on 2018-11-04.
 */

// This is AsyncTask used to login request to Cloud Server
public class SendGameResultAsyncTask extends AsyncTask<JSONObject, Void, String> {
    private final String TAG = "SendGameResultAsyncTask";
    private HttpURLConnection conn = null;
    private String returnMsg = "";
    private Context context = null;
    private URL url = null;

    private StringResponseCallback stringResponseCallback = null;

    public SendGameResultAsyncTask(Context context, String path, StringResponseCallback stringResponseCallback) {
        this.stringResponseCallback = stringResponseCallback;
        this.context = context;
        try {
            this.url = new URL(App.httpAddress + path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // Android socket client
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

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonData.toString());

            int status = conn.getResponseCode();
            Log.i(TAG, "status: "+status+ "HTTP_OK: "+HttpURLConnection.HTTP_OK);
            if(status == 200) {
                returnMsg = "OK";
            } else if(status == 409) {
                returnMsg = "ACCOUNT_ERROR";
                return returnMsg;
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
        Log.i(TAG, "on PostExecute() result:  " + result);
        stringResponseCallback.onResponseReceived(result);
    }
}
