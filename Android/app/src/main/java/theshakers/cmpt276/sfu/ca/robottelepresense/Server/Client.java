package theshakers.cmpt276.sfu.ca.robottelepresense.Server;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import theshakers.cmpt276.sfu.ca.robottelepresense.R;

/**
 * Created by baesubin on 2018-10-14.
 */

// client class connect to Pepper
public class Client extends AsyncTask <String, Void, String>{
    private final String TAG = "Client";
    private InetAddress inetAddress = null;
    private int serverPort = 9051;
    private DatagramSocket udpSocket = null;
    private String returnMsg = "";

    private ServerResponseCallback serverResponseCallback = null;

    public Client(ServerResponseCallback serverResponseCallback) {
        this.serverResponseCallback = serverResponseCallback;
    }


    // Android socket client
    @Override
    protected String doInBackground(String... params) {
        try {
            udpSocket = new DatagramSocket(serverPort);
            JSONObject jsonData = new JSONObject();
            jsonData.put("msg", params[0]);
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
                Log.i("Parsed to JSON: ", jsonObject.getString("msg"));
            } catch (Throwable tx) {
                Log.i("My App", "Could not parse malformed JSON: \"" + datafromPacket + "\"");
            }

        } catch (SocketException e) {
            Log.e(TAG, "SocketException, " + e);
            returnMsg = Integer.toString(R.string.connectionError);
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
        if(result.equals("")) {
            result = Integer.toString(R.string.clientResult1);
        }
        serverResponseCallback.onResponseReceived(result);
    }

}