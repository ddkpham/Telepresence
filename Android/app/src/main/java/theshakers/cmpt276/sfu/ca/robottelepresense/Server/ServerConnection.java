package theshakers.cmpt276.sfu.ca.robottelepresense.Server;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by baesubin on 2018-10-14.
 */

public class ServerConnection extends AsyncTask {
    private final String TAG = "ServerConnection";
    @Override
    protected Object doInBackground(Object[] objects) {
        buttonClicked();
        return null;
    }

    public void buttonClicked (){
        HttpURLConnection conn = null;
        try {

            URL url = new URL("http://10.0.2.2:5000/");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200)
               Log.i(TAG, "there is a problem with connection");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }
}
