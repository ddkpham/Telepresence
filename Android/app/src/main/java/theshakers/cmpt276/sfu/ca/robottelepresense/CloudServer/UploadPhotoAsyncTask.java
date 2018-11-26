package theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import theshakers.cmpt276.sfu.ca.robottelepresense.App;
import theshakers.cmpt276.sfu.ca.robottelepresense.R;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.UploadPhotoProgressListener;

/**
 * Created by baesubin on 2018-11-04.
 */

// This is AsyncTask used to upload photo to Flask Server
public class UploadPhotoAsyncTask extends AsyncTask<Object, Integer, String> implements UploadPhotoProgressListener {
    private final String TAG = "UploadPhotoAsyncTask";
    private ProgressDialog progressDialog;
    private Context context;
    private HashMap<String, String> param;
    private HashMap<String, String> files;
    private long startTime = 0;

    public UploadPhotoAsyncTask(Context context, HashMap<String, String> param, HashMap<String, String> files) {
        this.context = context;
        this.param = param;
        this.files = files;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(context.getString(R.string.sending_photo_to_pepper));
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Object... params) {
        String returnMsg = "";
        try {
            String url = App.httpAddress + "photo";
            MultipartUpload multipartUpload = new MultipartUpload(url, context, "UTF-8");
            multipartUpload.setUploadPhotoProgressListener(this);
            returnMsg = multipartUpload.upload(param, files);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnMsg;

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
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (result != null) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgressUpdate(int progress) {
        publishProgress(progress, 0);
    }
}
