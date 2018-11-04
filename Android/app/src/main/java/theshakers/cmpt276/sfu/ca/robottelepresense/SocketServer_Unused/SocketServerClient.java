package theshakers.cmpt276.sfu.ca.robottelepresense.SocketServer_Unused;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import theshakers.cmpt276.sfu.ca.robottelepresense.R;

/**
 * Created by baesubin on 2018-10-14.
 */

// The class for connecting to python server in Pepper
public class SocketServerClient extends AsyncTask <String, Void, String>{
    private final String TAG = "SocketServerClient";
    private InetAddress inetAddress = null;
    private int serverPort = 9051;
    private DatagramSocket udpSocket = null;
    private String returnMsg = "";
    private Context context;

    private SocketServerResponseCallback socketServerResponseCallback = null;

    public SocketServerClient(Context context, SocketServerResponseCallback socketServerResponseCallback) {
        this.socketServerResponseCallback = socketServerResponseCallback;
        this.context = context;
    }

    // Android socket client
    @Override
    protected String doInBackground(String... params) {
        try {
            udpSocket = new DatagramSocket(serverPort);
            JSONObject jsonData = new JSONObject();
            jsonData.put("msg", params[0]);
            Log.i(TAG, "sent message: " + params[0]);
            byte[] buf = jsonData.toString().getBytes();

            inetAddress = InetAddress.getByName("10.0.0.3");
            DatagramPacket udpPacket = new DatagramPacket(buf, buf.length, inetAddress, serverPort);
            udpSocket.send(udpPacket);

            byte[] receivedBuf = new byte[2048];

            DatagramPacket receivedPacket = new DatagramPacket(receivedBuf, receivedBuf.length);
            udpSocket.receive(receivedPacket);
            String datafromPacket = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            Log.i("UDP Packet received", datafromPacket );
            try {
                JSONObject jsonObject = new JSONObject(datafromPacket);
                returnMsg = jsonObject.getString("msg");
                Log.i(TAG, "Parsed to JSON: " + jsonObject.getString("msg"));
            } catch (Throwable tx) {
                Log.i(TAG, "Could not parse malformed JSON: " + datafromPacket );
            }

        } catch (SocketException e) {
            returnMsg = context.getResources().getString(R.string.error_connection);
            Log.e(TAG, "SocketException, " + e);
        } catch (IOException e) {
            Log.e(TAG, "IOException, " + e);
        } catch (Exception e) {
            Log.e(TAG, "Exception, " + e);
        } finally {
            udpSocket.close();
        }
        return returnMsg;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "result:  " + result);
        if(result.equals("")) {
            result = context.getResources().getString(R.string.error_wrong_attempt);
        }
        socketServerResponseCallback.onResponseReceived(result);
    }

}