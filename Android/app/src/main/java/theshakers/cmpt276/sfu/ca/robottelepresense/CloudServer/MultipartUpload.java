package theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.UploadPhotoProgressListener;
import theshakers.cmpt276.sfu.ca.robottelepresense.R;

/**
 * Created by baesubin on 2018-10-31.
 */

// This class is for creating packet string for sending photo in HTTP
public class MultipartUpload {
    private final String TAG = "MultipartUpload";
    private Context context = null;
    private final String boundary;
    private final String tail;
    private static final String LINE_END = "\r\n";
    private static final String TWOHYPEN = "--";
    private HttpURLConnection httpConn;
    private String charset;
    private PrintWriter writer;
    private OutputStream outputStream;
    private int maxBufferSize = 1024;
    private UploadPhotoProgressListener uploadPhotoProgressListener;
    private long startTime = 0;

    public MultipartUpload(String requestURL, Context context, String charset) throws IOException {
        this.context = context;
        this.charset = charset;
        boundary = "===" + System.currentTimeMillis() + "===";
        tail = LINE_END + TWOHYPEN + boundary + TWOHYPEN + LINE_END;
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    public void setUploadPhotoProgressListener(UploadPhotoProgressListener uploadPhotoProgressListener) {
        this.uploadPhotoProgressListener = uploadPhotoProgressListener;
    }

    public String upload(HashMap<String, String> params, HashMap<String, String> files) throws IOException {
        String paramsPart = "";
        String fileHeader = "";
        String filePart = "";
        long fileLength = 0;
        startTime = System.currentTimeMillis();


        ArrayList<String> paramHeaders = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            Log.i(TAG, "params: entry.getKey(): "+entry.getKey()+" entry.getValue(): "+entry.getValue());
            String param = TWOHYPEN + boundary + LINE_END
                    + "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END
                    + "Content-Type: text/plain; charset=" + charset + LINE_END
                    + LINE_END
                    + entry.getValue() + LINE_END;
            paramsPart += param;
            paramHeaders.add(param);
        }

        ArrayList<File> filesAL = new ArrayList<>();
        ArrayList<String> fileHeaders = new ArrayList<>();

        for (Map.Entry<String, String> entry : files.entrySet()) {
            Log.i(TAG, "params: entry.getKey(): "+entry.getKey()+" entry.getValue(): "+entry.getValue());
            File file = new File(entry.getValue());
            fileHeader = TWOHYPEN + boundary + LINE_END
                    + "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + file.getName() + "\"" + LINE_END
                    + "Content-Type: " + URLConnection.guessContentTypeFromName(file.getAbsolutePath()) + LINE_END
                    + "Content-Transfer-Encoding: binary" + LINE_END
                    + LINE_END;
            fileLength += file.length() + LINE_END.getBytes(charset).length;
            filePart += fileHeader;

            fileHeaders.add(fileHeader);
            filesAL.add(file);
        }
        String partData = paramsPart + filePart;

        long requestLength = partData.getBytes(charset).length + fileLength + tail.getBytes(charset).length;
        httpConn.setRequestProperty("Content-length", "" + requestLength);
        httpConn.setFixedLengthStreamingMode((int) requestLength);
        httpConn.connect();

        outputStream = new BufferedOutputStream(httpConn.getOutputStream());
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

        for (int i = 0; i < paramHeaders.size(); i++) {
            writer.append(paramHeaders.get(i));
            writer.flush();
        }

        int totalRead = 0;
        int bytesRead;
        byte buf[] = new byte[maxBufferSize];
        for (int i = 0; i < filesAL.size(); i++) {
            writer.append(fileHeaders.get(i));
            writer.flush();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filesAL.get(i)));
            while ((bytesRead = bufferedInputStream.read(buf)) != -1) {

                outputStream.write(buf, 0, bytesRead);
                writer.flush();
                totalRead += bytesRead;
                if (uploadPhotoProgressListener != null) {
                    float progress = (totalRead / (float) requestLength) * 100;
                    uploadPhotoProgressListener.onProgressUpdate((int) progress);
                }
            }
            outputStream.write(LINE_END.getBytes());
            //outputStream.flush();
            bufferedInputStream.close();
        }
        writer.append(tail);
        writer.flush();
        writer.close();

        String returnMsg = new String();
        int status = httpConn.getResponseCode();

        Log.i(TAG, "conn.getResponseCode(): "+status);
        if (status == 400) {
            returnMsg = context.getString(R.string.bad_request);
        } else if (status == 401) {
            returnMsg = context.getString(R.string.user_not_authorized_for_pep_id);
        } else if (status == 403) {
            returnMsg = context.getString(R.string.ask_check_failed_could_you_relogin);
        } else if (status == 409) {
            returnMsg = context.getString(R.string.user_does_not_exist);
        } else if (status == 410) {
            returnMsg = context.getString(R.string.failed_to_connect_to_pepper);
        } else if (status == 500) {
            returnMsg = context.getString(R.string.internal_server_error);
        } else if (status == 200) {
            returnMsg = context.getString(R.string.succeed);
        }

        httpConn.disconnect();
        return returnMsg;
    }

}

