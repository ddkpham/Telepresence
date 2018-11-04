package theshakers.cmpt276.sfu.ca.robottelepresense.WebServer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import theshakers.cmpt276.sfu.ca.robottelepresense.App;

/**
 * Created by baesubin on 2018-11-04.
 */

// This is AsyncTask used to upload photo to Flask Server
public class UploadPhotoAsyncTask extends AsyncTask<Object, Integer, JSONObject> implements UploadPhotoProgressListener {
    private final String TAG = "UploadAsync";
    private ProgressDialog progressDialog;
    private Context mContext;
    private HashMap<String, String> param;
    private HashMap<String, String> files;
    private long startTime;

    public UploadPhotoAsyncTask(Context context, HashMap<String, String> param, HashMap<String, String> files) {
        mContext = context;
        this.param = param;
        this.files = files;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("wait");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        JSONObject json = null;
        try {
            String url = App.httpAddress + "photo";
            MultipartUpload multipartUpload = new MultipartUpload(url, "UTF-8");
            multipartUpload.setUploadPhotoProgressListener(this);
            json = multipartUpload.upload(param, files);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressDialog != null && progressDialog.isShowing()) {
            if (values[1] == 1) {
                progressDialog.setProgress(values[0]);
            } else {
                progressDialog.setProgress(values[0]);
            }
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (result != null) {

            try {
                if (result.getInt("success") == 1) {
                    Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "connection error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgressUpdate(int progress) {
        publishProgress(progress, 0);
    }
}