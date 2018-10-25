package theshakers.cmpt276.sfu.ca.robottelepresense.Server;

/**
 * Created by baesubin on 2018-10-23.
 */

// This interface is for receiving the message from Server
public interface ServerResponseCallback {
    public void onResponseReceived(String result);
}
